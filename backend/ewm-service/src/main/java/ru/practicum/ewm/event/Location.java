package ru.practicum.ewm.event;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Location {
    private double lat;
    private double lon;
}
