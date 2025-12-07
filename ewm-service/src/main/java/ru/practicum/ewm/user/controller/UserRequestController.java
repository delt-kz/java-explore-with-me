package ru.practicum.ewm.user.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/requests")
public class UserRequestController {

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {

    }

    @PostMapping
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {

    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {

    }
}
