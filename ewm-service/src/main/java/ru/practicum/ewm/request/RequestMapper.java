package ru.practicum.ewm.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.util.Constants.dateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        String created = request.getCreated().format(dateTimeFormatter);
        return new ParticipationRequestDto(request.getId(),
                created,
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus());
    }

    public static List<ParticipationRequestDto> toDto(List<ParticipationRequest> requests) {
        return requests.stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    public static ParticipationRequest fromDto(ParticipationRequestDto dto, Event event, User requester) {
        ParticipationRequest request = new ParticipationRequest();
        request.setId(dto.getId());
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(dto.getStatus());
        request.setCreated(LocalDateTime.parse(dto.getCreated(), dateTimeFormatter));
        return request;
    }
}
