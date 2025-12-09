package ru.practicum.ewm.event;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> published() {
        return (root, query, cb) -> cb.equal(root.get("state"), "PUBLISHED");
    }

    public static Specification<Event> textSearch(String text) {
        if (text == null || text.isBlank()) return null;

        return (root, query, cb) -> {
            String pattern = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("annotation")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Event> categoriesIn(List<Long> categories) {
        if (categories == null || categories.isEmpty()) return null;
        return (root, query, cb) -> root.get("category").get("id").in(categories);
    }

    public static Specification<Event> paid(Boolean paid) {
        if (paid == null) return null;
        return (root, query, cb) -> cb.equal(root.get("paid"), paid);
    }

    public static Specification<Event> dateRange(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            final LocalDateTime actualStart = (start != null) ? start : LocalDateTime.now();
            if (end != null) {
                return cb.between(root.get("eventDate"), actualStart, end);
            } else {
                return cb.greaterThanOrEqualTo(root.get("eventDate"), actualStart);
            }
        };
    }


    public static Specification<Event> onlyAvailable(Boolean onlyAvailable) {
        if (onlyAvailable == null || !onlyAvailable) return null;
        return (root, query, cb) -> cb.greaterThan(root.get("participantLimit"), root.get("confirmedRequests"));
    }

    public static Specification<Event> combine(Specification<Event>... specs) {
        Specification<Event> result = Specification.where(null);
        for (Specification<Event> s : specs) {
            if (s != null) result = result.and(s);
        }
        return result;
    }
    public static Specification<Event> usersIn(List<Long> users) {
        if (users == null || users.isEmpty()) return null;
        return (root, query, cb) -> root.get("initiator").get("id").in(users);
    }

    public static Specification<Event> statesIn(List<EventState> states) {
        if (states == null || states.isEmpty()) return null;
        return (root, query, cb) -> root.get("state").in(states);
    }

}
