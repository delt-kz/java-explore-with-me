package ru.practicum.ewm.event.review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.Event;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_reviews")
@Getter
@Setter
public class EventReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(length = 2000, nullable = false)
    private String comment;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status; // например: RETURNED, APPROVED

    private LocalDateTime createdAt = LocalDateTime.now();
}
