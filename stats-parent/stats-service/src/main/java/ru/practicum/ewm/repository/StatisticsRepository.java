package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Hit, Long> {

    @Query("""
        SELECT new ru.practicum.ewm.dto.StatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))
        FROM Hit h
        WHERE h.timestamp BETWEEN :start AND :end
          AND h.uri IN :uris
        GROUP BY h.app, h.uri
    """)
    List<StatsDto> getStatsUnique(LocalDateTime start,
                                  LocalDateTime end,
                                  List<String> uris);


    @Query("""
        SELECT new ru.practicum.ewm.dto.StatsDto(h.app, h.uri, COUNT(h.ip))
        FROM Hit h
        WHERE h.timestamp BETWEEN :start AND :end
          AND h.uri IN :uris
        GROUP BY h.app, h.uri
    """)
    List<StatsDto> getStats(LocalDateTime start,
                                  LocalDateTime end,
                                  List<String> uris);
}
