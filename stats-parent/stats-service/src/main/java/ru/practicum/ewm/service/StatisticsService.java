package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.HitMapper;
import ru.practicum.ewm.model.Hit;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.mapper.HitMapper.dateTimeFormatter;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsRepository repo;

    @Transactional
    public void hit(HitDto dto) {
        Hit hit = HitMapper.fromDto(dto);
        repo.save(hit);
    }

    public List<StatsDto> getStats(String startString,
                                   String endString,
                                   List<String> uris,
                                   Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startString, dateTimeFormatter);
        LocalDateTime end = LocalDateTime.parse(endString, dateTimeFormatter);

        if (unique != null && unique) {
            return repo.getStatsUnique(start, end, uris);
        } else {
            return repo.getStats(start, end, uris);
        }
    }
}
