package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEvent_Id(Long eventId);

    List<ParticipationRequest> findAllByRequester_Id(Long userId);

    Boolean existsByEvent_IdAndRequester_Id(Long eventId, Long userId);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByIdInAndEvent_Id(List<Long> ids, Long eventId);

    @Modifying
    @Query("""
            UPDATE ParticipationRequest r SET r.status = :status WHERE r.id IN :requestIds
            """)
    void updateStatusByIdIn(List<Long> requestIds, RequestStatus status);

    @Query("""
            SELECT COUNT(r) = 0
            FROM ParticipationRequest r
            WHERE r.id IN :ids AND r.status <> ru.practicum.ewm.request.RequestStatus.PENDING
            """)
    Boolean areAllPending(List<Long> ids);

    @Modifying
    @Query("""
                UPDATE ParticipationRequest r
                SET r.status = ru.practicum.ewm.request.RequestStatus.REJECTED
                WHERE r.event.id = :eventId AND r.status = ru.practicum.ewm.request.RequestStatus.PENDING
            """)
    void rejectPendingByEventId(Long eventId);
}
