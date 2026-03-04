import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.repository.StatisticsRepository;
import ru.practicum.ewm.service.StatisticsService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {
    @Mock
    private StatisticsRepository repo;

    @InjectMocks
    private StatisticsService service;

    @Test
    void getStats_shouldThrowExceptionWhenStartAfterEnd() {
        String start = "2024-01-31 23:59:59";
        String end = "2024-01-01 00:00:00";

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            service.getStats(start, end, null, false);
        });

        assertEquals("Start date must be before end date", exception.getMessage());
    }

    @Test
    void getStats_shouldSortStatsByHitsDescending() {
        String start = "2024-01-01 00:00:00";
        String end = "2024-01-31 23:59:59";

        List<StatsDto> unsorted = new ArrayList<>(List.of(
                new StatsDto("ewm-service", "/events/2", 5L),
                new StatsDto("ewm-service", "/events/1", 20L),
                new StatsDto("ewm-service", "/events/3", 15L)
        ));

        when(repo.getStatsAll(any(), any())).thenReturn(unsorted);

        List<StatsDto> result = service.getStats(start, end, null, false);

        assertEquals(3, result.size());
        assertEquals(20L, result.get(0).getHits());
        assertEquals(15L, result.get(1).getHits());
        assertEquals(5L, result.get(2).getHits());
    }
}
