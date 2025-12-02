package ru.practicum.ewm.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {
    private double lat;
    private double lon;
}
