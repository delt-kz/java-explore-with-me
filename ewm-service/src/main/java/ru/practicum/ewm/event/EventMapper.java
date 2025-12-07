package ru.practicum.ewm.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
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

    public static Event fromUpdate(UpdateEventUserRequest dto, Event event) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(dto.getEventDate(), dateTimeFormatter));
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            event.setState(dto.getStateAction().equals("SEND_TO_REVIEW") ?
                    EventState.PENDING : EventState.CANCELED);
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        return event;
    }
}
