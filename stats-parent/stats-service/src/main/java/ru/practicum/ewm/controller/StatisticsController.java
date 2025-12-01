package ru.practicum.ewm.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.service.StatisticsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.OK)
    public void hit(@RequestBody HitDto hit) {
        service.hit(hit);
    }

    @GetMapping("/stats")
    public List<StatsDto> stats(@RequestParam String start,
                          @RequestParam String end,
                          @RequestParam(required = false) List<String> uris,
                          @RequestParam(required = false) Boolean unique) {
        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        return service.getStats(decodedStart, decodedEnd, uris, unique);
    }
}
