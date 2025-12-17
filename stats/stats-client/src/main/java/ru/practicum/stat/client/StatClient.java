package ru.practicum.stat.client;

import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatClient {
    void create(EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start,
                             LocalDateTime end,
                             List<String> uris,
                             boolean unique);
}
