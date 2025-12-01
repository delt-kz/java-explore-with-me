package ru.practicum.ewm.client;

import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

public class StatisticsClient {
    private final RestTemplate rest;
    private final String serverUrl;

    public StatisticsClient(String serverUrl) {
        this.rest = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    public void sendHit(HitDto hitDto) {
        rest.postForObject(serverUrl + "/hit", hitDto, Void.class);
    }

    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (uris != null) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        ResponseEntity<StatsDto[]> response =
                rest.getForEntity(builder.toUriString(), StatsDto[].class);

        return Arrays.asList(response.getBody());
    }
}
