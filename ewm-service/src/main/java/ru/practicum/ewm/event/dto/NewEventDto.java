package ru.practicum.ewm.event.dto;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.Location;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

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
