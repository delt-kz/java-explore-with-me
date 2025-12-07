package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.event.Location;

@Data
public class NewEventDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
