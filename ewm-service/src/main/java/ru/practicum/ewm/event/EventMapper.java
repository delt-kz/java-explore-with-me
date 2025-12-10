package ru.practicum.ewm.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.util.Constants.dateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventShortDto toShortDto(Event event) {
        String eventDate = event.getEventDate().format(dateTimeFormatter);
        CategoryDto categoryDto = CategoryMapper.toDto(event.getCategory());
        UserShortDto initiator = UserMapper.toShortDto(event.getInitiator());
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                eventDate,
                initiator,
                event.getPaid(),
                event.getTitle(),
                event.getViews());
    }

    public static List<EventShortDto> toShortDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }


    public static Event fromNew(NewEventDto dto, User initiator, Category category) {
        Event event = new Event();
        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), dateTimeFormatter);
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(eventDate);
        event.setLocation(dto.getLocation());
        event.setPaid(dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration());
        event.setTitle(dto.getTitle());
        event.setCategory(category);
        event.setInitiator(initiator);
        return event;
    }


    public static EventFullDto toFullDto(Event event) {
        String eventDate = event.getEventDate().format(dateTimeFormatter);
        String createdOn = event.getCreatedOn().format(dateTimeFormatter);
        String publishedOn = event.getPublishedOn() != null ? event.getPublishedOn().format(dateTimeFormatter) : null;
        CategoryDto categoryDto = CategoryMapper.toDto(event.getCategory());
        UserShortDto initiator = UserMapper.toShortDto(event.getInitiator());
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                createdOn,
                event.getDescription(),
                eventDate,
                initiator,
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                publishedOn,
                event.getRequestModeration(),
                event.getState().toString(),
                event.getTitle(),
                event.getViews());
    }

    public static List<EventFullDto> toFullDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    public static Event fromUpdate(UpdateEventUserRequest dto, Event event) {
        return updateEventBase(event,
                dto.getAnnotation(),
                dto.getDescription(),
                dto.getEventDate(),
                dto.getLocation(),
                dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration(),
                dto.getTitle());
    }

    public static Event fromUpdate(UpdateEventAdminRequest dto, Event event) {
        return updateEventBase(event,
                dto.getAnnotation(),
                dto.getDescription(),
                dto.getEventDate(),
                dto.getLocation(),
                dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration(),
                dto.getTitle());
    }




    private static Event updateEventBase(Event event, String annotation, String description, String eventDate,
                                         Location location, Boolean paid, Integer participantLimit,
                                         Boolean requestModeration, String title) {
        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (eventDate != null) event.setEventDate(LocalDateTime.parse(eventDate, dateTimeFormatter));
        if (location != null) event.setLocation(location);
        if (paid != null) event.setPaid(paid);
        if (participantLimit != null) event.setParticipantLimit(participantLimit);
        if (requestModeration != null) event.setRequestModeration(requestModeration);
        if (title != null) event.setTitle(title);
        return event;
    }

}
