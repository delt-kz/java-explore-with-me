package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.BusinessLogicException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestService {
    private final UserRepository userRepo;
    private final RequestRepository requestRepo;
    private final EventRepository eventRepo;

    public List<ParticipationRequestDto> getRequests(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        return requestRepo.findAllByRequester_Id(userId)
                .stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (requestRepo.existsByEvent_IdAndRequester_Id(eventId, userId)) {
            throw new BusinessLogicException("Request already exists");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new BusinessLogicException("Initiator cannot participate in own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BusinessLogicException("Event is not published");
        }

        if (event.getParticipantLimit() != 0 &&
                event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new BusinessLogicException("Participant limit reached");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(user);
        request.setEvent(event);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        return RequestMapper.toDto(requestRepo.save(request));
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        ParticipationRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new BusinessLogicException("You can cancel only your own requests");
        }

        if (request.getStatus() == RequestStatus.CANCELED) {
            return RequestMapper.toDto(request);
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toDto(requestRepo.save(request));
    }

}
