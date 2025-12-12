package ru.practicum.ewm.event.review;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.review.dto.EventReviewDto;

import java.util.List;

import static ru.practicum.ewm.util.Constants.dateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {

    public static EventReviewDto toDto(EventReview review) {
        String createdAt = review.getCreatedAt().format(dateTimeFormatter);
        return new EventReviewDto(review.getId(),
                review.getEvent().getId(),
                review.getComment(),
                review.getStatus(),
                createdAt);
    }

    public static List<EventReviewDto> toDto(List<EventReview> reviews) {
        return reviews.stream()
                .map(ReviewMapper::toDto)
                .toList();
    }
}
