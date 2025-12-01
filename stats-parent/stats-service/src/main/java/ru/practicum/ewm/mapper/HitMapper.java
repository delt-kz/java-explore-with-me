package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HitMapper {
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Hit fromDto(HitDto dto) {
        Hit hit = new Hit();
        hit.setIp(dto.getIp());
        hit.setUri(dto.getUri());
        hit.setApp(dto.getApp());
        return hit;
    }

    public static HitDto toDto(Hit hit) {
        return new HitDto(hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp().format(dateTimeFormatter));
    }
}
