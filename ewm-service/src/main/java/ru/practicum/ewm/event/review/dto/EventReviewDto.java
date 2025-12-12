package ru.practicum.ewm.event.review.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.event.review.ReviewStatus;

@Data
@AllArgsConstructor
public class EventReviewDto {
    private Long id;
    private Long eventId;
    private String comment;
    private ReviewStatus status;
    private String createdAt;
}
