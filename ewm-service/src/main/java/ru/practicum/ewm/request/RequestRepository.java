package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.Event;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEvent(Long eventId);

    List<ParticipationRequest> findAllByRequester(Long userId);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);
}
