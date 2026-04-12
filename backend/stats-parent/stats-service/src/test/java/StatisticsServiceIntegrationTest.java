import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.ewm.StatsApp;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatisticsRepository;
import ru.practicum.ewm.service.StatisticsService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest(classes = StatsApp.class)
@Transactional
@ActiveProfiles("test")
public class StatisticsServiceIntegrationTest {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private StatisticsRepository statisticsRepository;

    private HitDto hitDto;

    @BeforeEach
    void setUp() {
        statisticsRepository.deleteAll();

        hitDto = HitDto.builder()
                .app("test-app")
                .uri("/test")
                .ip("127.0.0.1")
                .timestamp("2024-01-15 12:00:00")
                .build();
    }

    @Test
    void hit_shouldCreateStatistics() {
        statisticsService.hit(hitDto);

        List<Hit> hits = statisticsRepository.findAll();

        assertEquals(1, hits.size());
        assertEquals(hitDto.getApp(), hits.get(0).getApp());
        assertEquals(hitDto.getUri(), hits.get(0).getUri());
        assertEquals(hitDto.getIp(), hits.get(0).getIp());
    }

    @Test
    void getStats_shouldReturnStatsWithDateFilterAndUris() {
        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 10:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.2")
                .timestamp("2024-01-10 11:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/2")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 12:00:00")
                .build());

        List<StatsDto> stats = statisticsService.getStats(
                "2024-01-10 00:00:00",
                "2024-01-10 23:59:59",
                List.of("/events/1"),
                false
        );

        assertEquals(1, stats.size());
        assertEquals("ewm-service", stats.get(0).getApp());
        assertEquals("/events/1", stats.get(0).getUri());
        assertEquals(2L, stats.get(0).getHits());
    }

    @Test
    void getStats_shouldReturnUniqueStatsWithDateFilterAndUris() {
        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 10:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 11:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.2")
                .timestamp("2024-01-10 12:00:00")
                .build());

        List<StatsDto> stats = statisticsService.getStats(
                "2024-01-10 00:00:00",
                "2024-01-10 23:59:59",
                List.of("/events/1"),
                true
        );

        assertEquals(1, stats.size());
        assertEquals(2L, stats.get(0).getHits());
    }

    @Test
    void getStats_shouldReturnAllStatsWithDateFilter() {
        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 10:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/2")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 11:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/2")
                .ip("192.168.1.2")
                .timestamp("2024-01-10 12:00:00")
                .build());

        List<StatsDto> stats = statisticsService.getStats(
                "2024-01-10 00:00:00",
                "2024-01-10 23:59:59",
                null,
                false
        );

        assertEquals(2, stats.size());
        assertEquals(2L, stats.get(0).getHits());
        assertEquals("/events/2", stats.get(0).getUri());
        assertEquals(1L, stats.get(1).getHits());
        assertEquals("/events/1", stats.get(1).getUri());
    }

    @Test
    void getStats_shouldReturnStatsWithoutDateFilter() {
        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-01-01 10:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.2")
                .timestamp("2024-12-31 10:00:00")
                .build());

        List<StatsDto> stats = statisticsService.getStats(
                null,
                null,
                List.of("/events/1"),
                false
        );

        assertEquals(1, stats.size());
        assertEquals(2L, stats.get(0).getHits());
    }

    @Test
    void getStats_shouldReturnUniqueStatsWithoutDateFilter() {
        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-01-01 10:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-12-31 10:00:00")
                .build());

        List<StatsDto> stats = statisticsService.getStats(
                null,
                null,
                List.of("/events/1"),
                true
        );

        assertEquals(1, stats.size());
        assertEquals(1L, stats.get(0).getHits());
    }

    @Test
    void getStats_shouldReturnEmptyListWhenNoData() {
        List<StatsDto> stats = statisticsService.getStats(
                "2024-01-10 00:00:00",
                "2024-01-10 23:59:59",
                List.of("/events/1"),
                false
        );

        assertTrue(stats.isEmpty());
    }

    @Test
    void getStats_shouldFilterByDateRange() {
        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-01-05 10:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.2")
                .timestamp("2024-01-15 10:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.3")
                .timestamp("2024-01-25 10:00:00")
                .build());

        List<StatsDto> stats = statisticsService.getStats(
                "2024-01-10 00:00:00",
                "2024-01-20 23:59:59",
                List.of("/events/1"),
                false
        );

        assertEquals(1, stats.size());
        assertEquals(1L, stats.get(0).getHits());
    }

    @Test
    void getStats_shouldSortByHitsDescending() {
        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/1")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 10:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/2")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 11:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/2")
                .ip("192.168.1.2")
                .timestamp("2024-01-10 12:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/2")
                .ip("192.168.1.3")
                .timestamp("2024-01-10 13:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/3")
                .ip("192.168.1.1")
                .timestamp("2024-01-10 14:00:00")
                .build());

        statisticsService.hit(HitDto.builder()
                .app("ewm-service")
                .uri("/events/3")
                .ip("192.168.1.2")
                .timestamp("2024-01-10 15:00:00")
                .build());

        List<StatsDto> stats = statisticsService.getStats(
                "2024-01-10 00:00:00",
                "2024-01-10 23:59:59",
                null,
                false
        );

        assertEquals(3, stats.size());
        assertEquals("/events/2", stats.get(0).getUri());
        assertEquals(3L, stats.get(0).getHits());
        assertEquals("/events/3", stats.get(1).getUri());
        assertEquals(2L, stats.get(1).getHits());
        assertEquals("/events/1", stats.get(2).getUri());
        assertEquals(1L, stats.get(2).getHits());
    }

    @Test
    void getStats_shouldThrowExceptionWhenStartAfterEnd() {
        assertThrows(BadRequestException.class, () -> {
            statisticsService.getStats(
                    "2024-01-31 23:59:59",
                    "2024-01-01 00:00:00",
                    null,
                    false
            );
        });
    }
}
