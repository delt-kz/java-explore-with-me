package ru.practicum.ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
}
