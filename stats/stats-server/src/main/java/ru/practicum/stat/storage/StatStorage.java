package ru.practicum.stat.storage;

import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatStorage {
    long saveHit(EndpointHit hit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
