package ru.practicum.ewm.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.BusinessLogicException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.util.CurrentUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private RequestRepository requestRepo;
    @Mock
    private EventRepository eventRepo;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private RequestService requestService;

    private User requester;
    private User initiator;
    private Event event;
    private ParticipationRequest request;

    @BeforeEach
    void setUp() {
        requester = User.builder().id(1L).name("Requester").build();
        initiator = User.builder().id(2L).name("Initiator").build();
        event = Event.builder()
                .id(1L)
                .initiator(initiator)
                .state(EventState.PUBLISHED)
                .participantLimit(10)
                .confirmedRequests(0)
                .requestModeration(true)
                .build();
        request = new ParticipationRequest();
        request.setId(1L);
        request.setRequester(requester);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);
    }

    @Test
    void getRequests_shouldReturnUserRequests() {
        when(currentUser.getUserId()).thenReturn(1L);
        when(requestRepo.findAllByRequester_Id(1L)).thenReturn(List.of(request));

        List<ParticipationRequestDto> result = requestService.getRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void createRequest_whenValid_shouldCreatePendingRequest() {
        when(currentUser.getUserId()).thenReturn(1L);
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(1L)).thenReturn(Optional.of(requester));
        when(requestRepo.existsByEvent_IdAndRequester_Id(1L, 1L)).thenReturn(false);
        when(requestRepo.save(any(ParticipationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ParticipationRequestDto result = requestService.createRequest(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.PENDING, result.getStatus());
    }

    @Test
    void createRequest_whenModerationOff_shouldCreateConfirmedRequest() {
        event.setRequestModeration(false);
        when(currentUser.getUserId()).thenReturn(1L);
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(1L)).thenReturn(Optional.of(requester));
        when(requestRepo.save(any(ParticipationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ParticipationRequestDto result = requestService.createRequest(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.CONFIRMED, result.getStatus());
        assertEquals(1, event.getConfirmedRequests());
    }

    @Test
    void createRequest_whenAlreadyExists_shouldThrowException() {
        when(currentUser.getUserId()).thenReturn(1L);
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(1L)).thenReturn(Optional.of(requester));
        when(requestRepo.existsByEvent_IdAndRequester_Id(1L, 1L)).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> requestService.createRequest(1L));
    }

    @Test
    void createRequest_whenInitiator_shouldThrowException() {
        when(currentUser.getUserId()).thenReturn(2L); // Initiator ID
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(2L)).thenReturn(Optional.of(initiator));

        assertThrows(BusinessLogicException.class, () -> requestService.createRequest(1L));
    }

    @Test
    void createRequest_whenNotPublished_shouldThrowException() {
        event.setState(EventState.PENDING);
        when(currentUser.getUserId()).thenReturn(1L);
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(1L)).thenReturn(Optional.of(requester));

        assertThrows(BusinessLogicException.class, () -> requestService.createRequest(1L));
    }

    @Test
    void createRequest_whenLimitReached_shouldThrowException() {
        event.setConfirmedRequests(10);
        when(currentUser.getUserId()).thenReturn(1L);
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(1L)).thenReturn(Optional.of(requester));

        assertThrows(BusinessLogicException.class, () -> requestService.createRequest(1L));
    }

    @Test
    void cancelRequest_whenValid_shouldCancel() {
        when(currentUser.getUserId()).thenReturn(1L);
        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepo.save(any(ParticipationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ParticipationRequestDto result = requestService.cancelRequest(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.CANCELED, result.getStatus());
    }

    @Test
    void cancelRequest_whenNotOwner_shouldThrowException() {
        when(currentUser.getUserId()).thenReturn(2L); // Not the owner
        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(BusinessLogicException.class, () -> requestService.cancelRequest(1L));
    }

    @Test
    void cancelRequest_whenNotFound_shouldThrowException() {
        when(requestRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.cancelRequest(1L));
    }
}
