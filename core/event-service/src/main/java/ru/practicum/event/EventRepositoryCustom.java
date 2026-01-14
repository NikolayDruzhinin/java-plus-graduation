package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {
    Page<Event> getEvents(String text,
                          List<Long> categories,
                          Boolean paid,
                          LocalDateTime rangeStart,
                          LocalDateTime rangeEnd,
                          Boolean onlyAvailable,
                          Boolean isAdmin,
                          Pageable pageable);
}

