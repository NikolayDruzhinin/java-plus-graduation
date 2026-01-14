package ru.practicum.application.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.application.request.model.EventRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<EventRequest, Long> {

    @Query("SELECT r FROM EventRequest r " +
            "WHERE r.requester = :userId ")
    List<EventRequest> findByUserId(Long userId);

    @Query("SELECT r FROM EventRequest r " +
            "WHERE r.event = :eventId")
    List<EventRequest> findByEventId(Long eventId);

    @Query("SELECT r FROM EventRequest r " +
            "WHERE r.event in :eventIds ")
    List<EventRequest> findByEventIds(List<Long> eventIds);

    List<EventRequest> findRequestByEventAndStatus(Long eventId, String status);

    @Query("SELECT COUNT(r) FROM EventRequest r " +
            "WHERE r.event = :eventId " +
            "AND r.status in :statuses")
    Long countByEventAndStatuses(Long eventId, List<String> statuses);

    @Query("SELECT r FROM EventRequest r " +
            "WHERE r.event in :eventIds " +
            "AND r.status = :status")
    List<EventRequest> findByEventIdsAndStatus(List<Long> eventIds, String status);

    @Query("SELECT COUNT(r) > 0 FROM EventRequest r WHERE r.requester = :requester AND r.event = :event")
    boolean userTakePart(Long requester, Long event);
}
