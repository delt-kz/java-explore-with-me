package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.mapper.HitMapper;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
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

        System.out.println(uris);
        boolean noDateFilter = (startString == null || startString.isBlank())
                && (endString == null || endString.isBlank());

        List<StatsDto> stats;

        if (noDateFilter) {
            if (uris == null || uris.isEmpty()) {
                stats = (unique != null && unique)
                        ? repo.getStatsUniqueAllNoDate()
                        : repo.getStatsAllNoDate();
            } else {
                stats = (unique != null && unique)
                        ? repo.getStatsUniqueNoDate(uris)
                        : repo.getStatsNoDate(uris);
            }
            stats.sort(Comparator.comparingLong(StatsDto::getHits).reversed());
            System.out.println(stats);
            return stats;
        }

        LocalDateTime start = LocalDateTime.parse(startString, dateTimeFormatter);
        LocalDateTime end = LocalDateTime.parse(endString, dateTimeFormatter);

        if (start.isAfter(end)) {
            throw new BadRequestException("Start date must be before end date");
        }

        if (uris == null || uris.isEmpty()) {
            stats = (unique != null && unique)
                    ? repo.getStatsUniqueAll(start, end)
                    : repo.getStatsAll(start, end);
        } else {
            stats = (unique != null && unique)
                    ? repo.getStatsUnique(start, end, uris)
                    : repo.getStats(start, end, uris);
        }
        System.out.println(stats);

        stats.sort(Comparator.comparingLong(StatsDto::getHits).reversed());
        return stats;
    }


}
