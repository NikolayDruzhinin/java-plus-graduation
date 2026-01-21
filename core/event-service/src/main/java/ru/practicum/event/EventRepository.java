package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    List<Event> findAllByInitiatorId(long userId, Pageable page);

    Optional<Event> findByInitiatorIdAndId(long userId, long eventId);

    List<Event> findAllByCategory(Long categoryId);

    List<Event> findByIdIn(Set<Long> eventIds);
}
