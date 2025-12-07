package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
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

        Page<Event> events = eventRepo.findAllByUserId(userId, pageRequest);
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
        int willApprove = dto.getStatus() == APPROVED ? requests.size() : 0;

        if (currentlyConfirmed + willApprove > event.getParticipantLimit()) {
            throw new BusinessLogicException("Event participant limit exceeded");
        }

        requests.forEach(r -> r.setStatus(dto.getStatus()));

        if (dto.getStatus() == APPROVED) {
            event.setConfirmedRequests(currentlyConfirmed + willApprove);
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            requestRepo.rejectPendingByEventId(eventId);
        }

        List<ParticipationRequestDto> confirmed =
                RequestMapper.toDto(requestRepo.findAllByEventIdAndStatus(eventId, APPROVED));
        List<ParticipationRequestDto> rejected =
                RequestMapper.toDto(requestRepo.findAllByEventIdAndStatus(eventId, REJECTED));

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmed);
        result.setRejectedRequests(rejected);

        return result;
    }

}
