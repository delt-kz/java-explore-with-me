package ru.practicum.ewm.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.util.Constants.dateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventShortDto toShortDto(Event event) {
        String eventDate = event.getEventDate().format(dateTimeFormatter);
        CategoryDto categoryDto = CategoryMapper.toDto(event.getCategory());
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                eventDate,
                event.getInitiator(),
                event.getPaid(),
                event.getTitle(),
                event.getViews());
    }

    public static List<EventShortDto> toShortDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

}
