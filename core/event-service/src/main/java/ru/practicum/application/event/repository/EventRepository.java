package ru.practicum.application.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.application.api.dto.enums.EventState;
import ru.practicum.application.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByIdIn(List<Long> ids);

    List<Event> findAllByCategory(Long catId);

    Page<Event> findAll(Pageable page);

    List<Event> findAllByInitiator(Long user, Pageable page);

    @Query(value = "SELECT e FROM Event e " +
            "WHERE e.initiator IN :users " +
            "AND e.state IN :states " +
            "AND e.category IN :categories " +
            "AND e.eventDate < :rangeStart " +
            "LIMIT :limitSize", nativeQuery = true)
    List<Event> findByParametersWithoutEnd(List<Long> users, List<String> states, List<Long> categories, String rangeStart, Integer limitSize);

    @Query(value = "SELECT e FROM Event e " +
            "WHERE e.initiator IN :users " +
            "AND e.state IN :states " +
            "AND e.category IN :categories " +
            "AND e.eventDate < :rangeStart " +
            "AND e.eventDate > :rangeEnd " +
            "LIMIT :limitSize", nativeQuery = true)
    List<Event> findByParametersWithEnd(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer limitSize);

    @Query("SELECT e FROM Event e WHERE e.initiator IN :users " +
            "AND e.state in :states " +
            "AND e.category in :categories " +
            "AND e.eventDate between :rangeStart AND :rangeEnd ")
    List<Event> findAllEventsWithDates(List<Long> users,
                                       List<EventState> states,
                                       List<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Pageable page);

    @Query("SELECT e FROM Event e " +
            "WHERE e.category in :categories " +
            "AND e.state = :state")
    List<Event> findAllByCategoryIdPageable(List<Long> categories, EventState state, Pageable page);

    @Query("SELECT e FROM Event e " +
            "WHERE (lower(e.annotation) LIKE :text " +
            "OR lower(e.description) LIKE :text) " +
            "AND e.state = :state")
    List<Event> findEventsByText(String text, EventState state, Pageable page);

    @Query("SELECT e FROM Event e " +
            "WHERE (lower(e.annotation) LIKE :text " +
            "OR lower(e.description) LIKE :text) " +
            "AND e.eventDate >= :startDate " +
            "AND e.eventDate <= :endDate " +
            "AND e.state = :state")
    List<Event> findAllByTextAndDateRange(String text, LocalDateTime startDate, LocalDateTime endDate, EventState state, Pageable page);

    @Query("SELECT DISTINCT e FROM Event e " +
            "WHERE (e.annotation LIKE COALESCE(:text, e.annotation) OR e.description LIKE COALESCE(:text, e.description)) " +
            "AND (:categories IS NULL OR e.category IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND e.eventDate >= COALESCE(:rangeStart, e.eventDate) " +
            "AND e.eventDate <= COALESCE(:rangeEnd, e.eventDate) " +
            "AND e.state = :state " +
            "ORDER BY e.eventDate DESC")
    List<Event> findEventList(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, EventState state);

    boolean existsByCategory(Long catId);
}
