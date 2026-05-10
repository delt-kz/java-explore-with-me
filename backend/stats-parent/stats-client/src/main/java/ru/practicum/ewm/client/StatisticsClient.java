package ru.practicum.ewm.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;

import java.util.List;

public class StatisticsClient {
    private final RestClient restClient;

    public StatisticsClient(String serverUrl) {
        restClient = RestClient.create(serverUrl);
    }

    public void sendHit(HitDto hitDto) {
        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("unique", unique);

                    if (uris != null && !uris.isEmpty()) {
                        uriBuilder.queryParam("uris", uris);
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>(){});
    }
}
