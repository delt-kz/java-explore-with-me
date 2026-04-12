package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.request.RequestStatus;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private String created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}
