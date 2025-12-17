package ru.practicum.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.event.entity.Event;
import ru.practicum.main.event.entity.EventState;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndState(Long id, EventState state);

    @Modifying
    @Query("UPDATE Event e SET e.views = :views WHERE e.id = :eventId")
    void setViews(@Param("eventId") Long eventId, @Param("views") Long views);
}
