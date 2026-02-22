package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.review.dto.EventReviewDto;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/me/events")
@RequiredArgsConstructor
public class UserEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllEvents(from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto dto) {
        return eventService.createEvent(dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest dto) {
        return eventService.updateEvent(eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long eventId) {
        return eventService.getEventRequests(eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult manageRequests(@PathVariable Long eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest dto) {
        return eventService.manageRequests(eventId, dto);
    }

    @GetMapping("/{eventId}/reviews")
    public List<EventReviewDto> getReviews(@PathVariable Long eventId) {
        return eventService.getReviews(eventId);
    }
}
