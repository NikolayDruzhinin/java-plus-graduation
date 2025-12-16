package ru.practicum.main.views.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.views.model.EventView;

public interface EventViewRepository extends JpaRepository<EventView, Long> {
    boolean existsByIpAndEventId(String ip, Long eventId);

    Long countByEventId(Long eventId);
}

