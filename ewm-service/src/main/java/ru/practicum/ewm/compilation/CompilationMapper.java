package ru.practicum.ewm.compilation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static Compilation fromNew(NewCompilationDto dto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());
        return compilation;
    }

    public static CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> events = EventMapper.toShortDto(compilation.getEvents());
        return new CompilationDto(compilation.getId(), events, compilation.getPinned(), compilation.getTitle());
    }

    public static List<CompilationDto> toDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toDto)
                .toList();
    }

    public static Compilation fromUpdate(UpdateCompilationDto dto, Compilation compilation, List<Event> events) {
        if (dto.hasPinned()) compilation.setPinned(dto.getPinned());
        if (dto.hasTitle()) compilation.setTitle(dto.getTitle());
        if (!events.isEmpty()) compilation.setEvents(events);
        return compilation;
    }
}
