package ru.practicum.ewm.service;

import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.ViewStatsOutputDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    void createEndpointHit(EndpointHitInputDto endpointHitInputDto);

    List<ViewStatsOutputDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, Boolean unique);
}
