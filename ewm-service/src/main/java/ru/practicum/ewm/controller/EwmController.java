package ru.practicum.ewm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.client.StatisticsClient;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;

import java.util.List;

@RestController
@RequestMapping
public class EwmController {
    @GetMapping
    public List<StatsDto> get() {
        StatisticsClient client = new StatisticsClient("http://stats-service:9090");
        HitDto dto = new HitDto(null, "ewm", "mama", "4324.24423.2", "2022-09-06 11:00:23");
        client.sendHit(dto);
        return client.getStats("2021-09-06 11:00:23", "2027-09-06 11:00:23", List.of("mama"), true);
    }
}
