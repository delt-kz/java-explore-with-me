package ru.practicum.ewm.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompilationServiceTest {

    @Mock
    private CompilationRepository compilationRepo;
    @Mock
    private EventRepository eventRepo;

    @InjectMocks
    private CompilationService compilationService;

    private Compilation compilation;
    private Event event;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .id(1L)
                .title("Event Title")
                .eventDate(LocalDateTime.now().plusDays(1))
                .build();
        // minimal linked objects required by mappers
        Category category = new Category();
        category.setId(100L);
        category.setName("Cat");
        event.setCategory(category);
        User initiator = User.builder().id(200L).name("Init").email("i@i.com").build();
        event.setInitiator(initiator);

        compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Compilation Title");
        compilation.setPinned(false);
        compilation.setEvents(List.of(event));
    }

    @Test
    void createCompilation_shouldSaveAndReturnDto() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setTitle("New Compilation");
        dto.setEvents(List.of(1L));

        when(eventRepo.findAllById(List.of(1L))).thenReturn(List.of(event));
        when(compilationRepo.save(any(Compilation.class))).thenAnswer(invocation -> {
            Compilation saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        CompilationDto result = compilationService.createCompilation(dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("New Compilation", result.getTitle());
        verify(compilationRepo).save(any(Compilation.class));
    }

    @Test
    void deleteCompilation_shouldCallRepository() {
        compilationService.deleteCompilation(1L);
        verify(compilationRepo).deleteById(1L);
    }

    @Test
    void updateCompilation_whenFound_shouldUpdateAndSave() {
        UpdateCompilationDto updateDto = new UpdateCompilationDto();
        updateDto.setTitle("Updated Title");
        updateDto.setPinned(true);

        when(compilationRepo.findById(1L)).thenReturn(Optional.of(compilation));
        when(compilationRepo.save(any(Compilation.class))).thenReturn(compilation);

        CompilationDto result = compilationService.updateCompilation(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated Title", compilation.getTitle());
        assertTrue(compilation.getPinned());
    }

    @Test
    void updateCompilation_whenNotFound_shouldThrowException() {
        UpdateCompilationDto updateDto = new UpdateCompilationDto();
        when(compilationRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.updateCompilation(1L, updateDto));
    }

    @Test
    void getAllCompilations_shouldReturnDtoList() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Compilation> page = new PageImpl<>(List.of(compilation));
        when(compilationRepo.findAllByPinned(pageRequest, false)).thenReturn(page);

        List<CompilationDto> result = compilationService.getAllCompilations(false, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getCompilation_whenFound_shouldReturnDto() {
        when(compilationRepo.findById(1L)).thenReturn(Optional.of(compilation));

        CompilationDto result = compilationService.getCompilation(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCompilation_whenNotFound_shouldThrowException() {
        when(compilationRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.getCompilation(1L));
    }
}
