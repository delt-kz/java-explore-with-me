package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.BusinessLogicException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.ParticipationRequest;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.request.RequestStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final RequestRepository requestRepo;

    public List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);

        Page<Event> events = eventRepo.findAllByInitiator_Id(userId, pageRequest);
        return EventMapper.toShortDto(events.getContent());
    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        Category category = categoryRepo.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        User initiator = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = EventMapper.fromNew(dto, initiator, category);

        if (event.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException("Event date must be in the future");
        }

        return EventMapper.toFullDto(eventRepo.save(event));
    }

    public EventFullDto getEvent(Long id) {
        return EventMapper.toFullDto(eventRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found")));
    }

    public EventFullDto getEventPublic(Long id) {
        Event event = eventRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new BusinessLogicException("Event is not published");
        }
        return EventMapper.toFullDto(event);
    }

    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BusinessLogicException("You can't edit this event");
        }

        if (event.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException("Event date must be in the future");
        }

        if (!(event.getState().equals(EventState.CANCELED) || event.getState().equals(EventState.PENDING))) {
            throw new BusinessLogicException("Event is not pending or canceled");
        }

        if (dto.hasCategory()) {
            event.setCategory(categoryRepo.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found")));
        }

        return EventMapper.toFullDto(eventRepo.save(EventMapper.fromUpdate(dto, event)));
    }

    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        return RequestMapper.toDto(requestRepo.findAllByEvent_Id(eventId));
    }


    @Transactional
    public EventRequestStatusUpdateResult manageRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new BusinessLogicException("You can't manage requests for this event");
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new BusinessLogicException("Request moderation is disabled");
        }

        List<ParticipationRequest> requests =
                requestRepo.findAllByIdInAndEvent_Id(dto.getRequestIds(), eventId);

        if (requests.size() != dto.getRequestIds().size()) {
            throw new BusinessLogicException("Some requests not found for this event");
        }

        if (requests.stream().anyMatch(r -> r.getStatus() != PENDING)) {
            throw new BusinessLogicException("Not all requests are pending");
        }

        int currentlyConfirmed = event.getConfirmedRequests();
        int willApprove = dto.getStatus() == CONFIRMED ? requests.size() : 0;

        if (currentlyConfirmed + willApprove > event.getParticipantLimit()) {
            throw new BusinessLogicException("Event participant limit exceeded");
        }

        requests.forEach(r -> r.setStatus(dto.getStatus()));

        if (dto.getStatus() == CONFIRMED) {
            event.setConfirmedRequests(currentlyConfirmed + willApprove);
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            requestRepo.rejectPendingByEventId(eventId);
        }

        List<ParticipationRequestDto> confirmed =
                RequestMapper.toDto(requestRepo.findAllByEventIdAndStatus(eventId, CONFIRMED));
        List<ParticipationRequestDto> rejected =
                RequestMapper.toDto(requestRepo.findAllByEventIdAndStatus(eventId, REJECTED));

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmed);
        result.setRejectedRequests(rejected);

        return result;
    }

    public List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size
    ) {
        Specification<Event> spec = EventSpecifications.combine(
                EventSpecifications.published(),
                EventSpecifications.textSearch(text),
                EventSpecifications.categoriesIn(categories),
                EventSpecifications.paid(paid),
                EventSpecifications.dateRange(rangeStart, rangeEnd),
                EventSpecifications.onlyAvailable(onlyAvailable)
        );

        Pageable pageable;
        if ("VIEWS".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "views"));
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        }

        return EventMapper.toShortDto(eventRepo.findAll(spec, pageable).getContent());
    }

        //ADMIN


    public List<EventFullDto> getEvents(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    ) {
        Specification<Event> spec = EventSpecifications.combine(
                EventSpecifications.usersIn(users),
                EventSpecifications.statesIn(states),
                EventSpecifications.categoriesIn(categories),
                EventSpecifications.dateRange(rangeStart, rangeEnd)
        );

        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepo.findAll(spec, pageable).getContent();

        return EventMapper.toFullDto(eventRepo.findAll(spec, pageable).getContent());
    }

    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (dto.hasStateAction()) {
            if (dto.getStateAction().equals("PUBLISH_EVENT")) {
                if (event.getState() != EventState.PENDING) {
                    throw new BusinessLogicException("Event can only be published when in PENDING state");
                }
                if (event.getEventDate().minusHours(1).isBefore(LocalDateTime.now())) {
                    throw new BusinessLogicException("Event must start at least 1 hour after publication");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (dto.getStateAction().equals("REJECT_EVENT")) {
                if (event.getState() == EventState.PUBLISHED || event.getState() == EventState.CANCELED) {
                    throw new BusinessLogicException("Cannot reject published or canceled event");
                }
                event.setState(EventState.CANCELED);
            }
        }

        if (dto.hasCategory()) {
            event.setCategory(categoryRepo.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found")));
        }

        return EventMapper.toFullDto(eventRepo.save(EventMapper.fromUpdate(dto, event)));
    }

}
