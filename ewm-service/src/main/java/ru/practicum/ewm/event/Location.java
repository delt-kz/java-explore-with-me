package ru.practicum.ewm.event;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {
    private double lat;
    private double lon;
}
