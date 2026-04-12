package ru.practicum.ewm.event;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.client.StatisticsClient;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.BusinessLogicException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.ParticipationRequest;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.util.CurrentUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.ewm.util.Constants.dateTimeFormatter;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private CategoryRepository categoryRepo;
    @Mock
    private RequestRepository requestRepo;
    @Mock
    private StatisticsClient statisticsClient;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private EventService eventService;

    private User user;
    private Category category;
    private NewEventDto newEventDto;
    private Event event;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Location location = new Location();
        location.setLat(55.75);
        location.setLon(37.61);

        newEventDto = NewEventDto.builder()
                .annotation("Test annotation for the event, must be long enough.")
                .category(1L)
                .description("Test description for the event, also must be long enough.")
                .eventDate(LocalDateTime.now().plusDays(1).format(dateTimeFormatter))
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(true)
                .title("Test Title")
                .build();

        event = EventMapper.fromNew(newEventDto, user, category);
        event.setId(1L);
    }

    @Test
    void createEvent_whenValid_shouldReturnEventFullDto() {
        when(currentUser.getUserId()).thenReturn(1L);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepo.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventFullDto result = eventService.createEvent(newEventDto);

        assertNotNull(result);
        assertEquals(event.getTitle(), result.getTitle());
        assertEquals(event.getDescription(), result.getDescription());
        assertEquals(event.getLocation().getLat(), result.getLocation().getLat());
        assertEquals(event.getDescription(), result.getDescription());
        verify(eventRepo, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_whenCategoryNotFound_shouldThrowNotFoundException() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.createEvent(newEventDto));
        verify(eventRepo, never()).save(any(Event.class));
    }

    @Test
    void createEvent_whenUserNotFound_shouldThrowNotFoundException() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(currentUser.getUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.createEvent(newEventDto));
        verify(eventRepo, never()).save(any(Event.class));
    }

    @Test
    void createEvent_whenDateTooEarly_shouldThrowBadRequestException() {
        newEventDto.setEventDate(LocalDateTime.now().plusHours(1).format(dateTimeFormatter));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(currentUser.getUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> eventService.createEvent(newEventDto));
        verify(eventRepo, never()).save(any(Event.class));
    }

    @Test
    void getEvent_whenExists_shouldReturnEvent() {
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));

        EventFullDto result = eventService.getEvent(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepo, times(1)).findById(1L);
    }

    @Test
    void getEvent_whenNotFound_shouldThrowNotFoundException() {
        when(eventRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.getEvent(1L));
        verify(eventRepo, times(1)).findById(1L);
    }

    @Test
    void getAllEvents_shouldReturnListOfShortDtos() {
        when(currentUser.getUserId()).thenReturn(1L);
        Page<Event> page = new PageImpl<>(List.of(event));
        when(eventRepo.findAllByInitiator_Id(eq(1L), any(PageRequest.class))).thenReturn(page);

        List<EventShortDto> result = eventService.getAllEvents(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event.getId(), result.get(0).getId());
    }

    @Test
    void updateEvent_User_whenValid_shouldUpdateEvent() {
        UpdateEventUserRequest updateDto = new UpdateEventUserRequest();
        updateDto.setAnnotation("New annotation that is long enough to pass validation rules.");
        updateDto.setStateAction(StateAction.SEND_TO_REVIEW);

        event.setState(EventState.CANCELED);

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(currentUser.getUserId()).thenReturn(1L);
        when(eventRepo.save(any(Event.class))).thenReturn(event);

        EventFullDto result = eventService.updateEvent(1L, updateDto);

        assertNotNull(result);
        assertEquals(EventState.PENDING, event.getState());
        verify(eventRepo).save(any(Event.class));
    }

    @Test
    void updateEvent_User_whenNotInitiator_shouldThrowException() {
        UpdateEventUserRequest updateDto = new UpdateEventUserRequest();
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(currentUser.getUserId()).thenReturn(2L);

        assertThrows(BusinessLogicException.class, () -> eventService.updateEvent(1L, updateDto));
    }

    @Test
    void updateEvent_Admin_whenPublish_shouldWork() {
        UpdateEventAdminRequest updateDto = new UpdateEventAdminRequest();
        updateDto.setStateAction(StateAction.PUBLISH_EVENT);
        event.setState(EventState.PENDING);

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepo.save(any(Event.class))).thenReturn(event);

        EventFullDto result = eventService.updateEvent(1L, updateDto);

        assertNotNull(result);
        assertEquals(EventState.PUBLISHED, event.getState());
    }

    @Test
    void manageRequests_whenConfirm_shouldWork() {
        EventRequestStatusUpdateRequest updateDto = new EventRequestStatusUpdateRequest();
        updateDto.setRequestIds(List.of(1L));
        updateDto.setStatus(RequestStatus.CONFIRMED);

        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setConfirmedRequests(0);

        ParticipationRequest request = new ParticipationRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        request.setEvent(event);
        request.setRequester(user);

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(currentUser.getUserId()).thenReturn(1L);
        when(requestRepo.findAllByIdInAndEvent_Id(anyList(), eq(1L))).thenReturn(List.of(request));
        when(requestRepo.findAllByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(List.of(request));
        when(requestRepo.findAllByEventIdAndStatus(1L, RequestStatus.REJECTED)).thenReturn(List.of());

        EventRequestStatusUpdateResult result = eventService.manageRequests(1L, updateDto);

        assertNotNull(result);
        assertEquals(1, result.getConfirmedRequests().size());
        assertEquals(RequestStatus.CONFIRMED, request.getStatus());
    }

    @Test
    void getEventPublic_whenPublished_shouldWork() {
        event.setState(EventState.PUBLISHED);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/events/1");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        StatsDto statsDto = new StatsDto("ewm-service", "/events/1", 10L);
        when(statisticsClient.getStats(any(), any(), anyList(), eq(true))).thenReturn(List.of(statsDto));

        EventFullDto result = eventService.getEventPublic(1L, mockRequest);

        assertNotNull(result);
        assertEquals(10L, result.getViews());
        verify(statisticsClient).sendHit(any());
    }
}
