package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.request.entity.ParticipationRequest;
import ru.practicum.main.request.entity.RequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByIdInAndEventId(Collection<Long> ids, Long eventId);

    Optional<ParticipationRequest> findByIdAndRequesterId(Long id, Long requesterId);

    @Modifying
    @Query("update ParticipationRequest r set r.status = ?2 where r.eventId = ?1 and r.status = 'PENDING'")
    int rejectAllPendingForEvent(Long eventId, RequestStatus newStatus);
}

