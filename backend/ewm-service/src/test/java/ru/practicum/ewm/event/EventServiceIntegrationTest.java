package ru.practicum.ewm.event;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.ewm.EwmApp;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.util.CurrentUser;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ru.practicum.ewm.util.Constants.dateTimeFormatter;

@SpringBootTest(classes = EwmApp.class)
@Transactional
@ActiveProfiles("test")
public class EventServiceIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private CurrentUser currentUser;

    private User user;
    private Category category;
    private NewEventDto newEventDto;

    @BeforeEach
    public void setup() {
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("test@test.com");
        user = userRepository.save(newUser);
        when(currentUser.getUserId()).thenReturn(user.getId());

        Category newCategory = new Category();
        newCategory.setName("Test Category");
        category = categoryRepository.save(newCategory);

        Location location = new Location();
        location.setLat(55.75);
        location.setLon(37.61);

        newEventDto = NewEventDto.builder()
                .annotation("Test annotation for the event, must be long enough.")
                .category(category.getId())
                .description("Test description for the event, also must be long enough.")
                .eventDate(LocalDateTime.now().plusDays(1).format(dateTimeFormatter))
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(true)
                .title("Test Title")
                .build();
    }

    @Test
    void createEvent_shouldPersistEvent() {
        EventFullDto result = eventService.createEvent(newEventDto);

        Event saved = eventRepository.findById(result.getId()).orElseThrow();
        assertEquals(newEventDto.getAnnotation(), saved.getAnnotation());
        assertEquals(user.getId(), saved.getInitiator().getId());
        assertEquals(category.getId(), saved.getCategory().getId());
    }

    @Test
    void getEvent_whenExists_shouldReturnEvent() {
        EventFullDto result = eventService.createEvent(newEventDto);

        final EventFullDto[] retrieved = new EventFullDto[1];
        assertDoesNotThrow(() -> retrieved[0] = eventService.getEvent(result.getId()));
        assertEquals(newEventDto.getAnnotation(),  retrieved[0].getAnnotation());
        assertEquals(user.getId(), retrieved[0].getInitiator().getId());
        assertEquals(category.getId(), retrieved[0].getCategory().getId());
    }

    @Test
    void getEvent_whenNotExists_shouldReturnNull() {
        assertThrows(NotFoundException.class, () -> eventService.getEvent(-2L));
    }

    @Test
    void updateEvent_shouldUpdateEvent() {
        EventFullDto result = eventService.createEvent(newEventDto);

        UpdateEventUserRequest updateRequest = UpdateEventUserRequest.builder()
                .annotation("Женский стендап")
                .build();

        EventFullDto updated = eventService.updateEvent(result.getId(), updateRequest);


        assertNotNull(updated);
        assertEquals(result.getId(), updated.getId());
        assertEquals(updateRequest.getAnnotation(), updated.getAnnotation());
    }
}
