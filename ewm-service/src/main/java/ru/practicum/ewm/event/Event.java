package ru.practicum.ewm.event;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.event.review.EventReview;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2000, nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private Integer confirmedRequests = 0;
    private LocalDateTime createdOn = LocalDateTime.now();
    @Column(length = 7000, nullable = false)
    private String description;
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Embedded
    private Location location;
    private Boolean paid = false;
    private Integer participantLimit = 0;
    private LocalDateTime publishedOn;
    private Boolean requestModeration = true;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state = EventState.PENDING;
    @Column(length = 120, nullable = false)
    private String title;
    private Long views = 0L;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventReview> reviews = new ArrayList<>();

}