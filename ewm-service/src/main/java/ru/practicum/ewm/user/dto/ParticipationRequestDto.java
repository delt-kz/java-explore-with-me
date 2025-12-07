package ru.practicum.ewm.user.dto;

import ch.qos.logback.core.status.Status;
import lombok.Data;
import ru.practicum.ewm.user.RequestStatus;

@Data
public class ParticipationRequestDto {
    private Long id;
    private String created;
    private Integer event;
    private Integer requester;
    private RequestStatus status;
}
