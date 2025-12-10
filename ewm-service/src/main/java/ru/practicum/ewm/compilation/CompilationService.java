package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepo;
    private final EventRepository eventRepo;

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        List<Long> eventIds = dto.getEvents().stream()
                .filter(Objects::nonNull)
                .toList();

        List<Event> events = eventRepo.findAllById(eventIds);
        Compilation compilation = CompilationMapper.fromNew(dto, events);
        return CompilationMapper.toDto(compilationRepo.save(compilation));
    }

    @Transactional
    public void deleteCompilation(Long id) {
        compilationRepo.deleteById(id);
    }

    @Transactional
    public CompilationDto updateCompilation(Long id, UpdateCompilationDto dto) {
        List<Event> events = List.of();
        if (dto.hasEvents()) {
            events = eventRepo.findAllById(dto.getEvents());
        }
        Compilation compilation = compilationRepo.findById(id).orElseThrow(() -> new NotFoundException("Compilation not found"));
        CompilationMapper.fromUpdate(dto, compilation, events);
        return CompilationMapper.toDto(compilationRepo.save(compilation));
    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<Compilation> page = compilationRepo.findAllByPinned(pageRequest, pinned);
        List<Compilation> compilations = page.getContent();
        return CompilationMapper.toDto(compilations);
    }

    public CompilationDto getCompilation(Long id) {
        return CompilationMapper.toDto(
                compilationRepo.findById(id)
                        .orElseThrow(() -> new NotFoundException("Compilation not found"))
        );
    }
}
