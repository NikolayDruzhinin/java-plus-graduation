package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.repository.EndpointHitRepository;
import ru.practicum.ewm.ViewStatsOutputDto;
import ru.practicum.ewm.mapper.EndpointHitMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticServiceImpl implements StatisticService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public void createEndpointHit(EndpointHitInputDto endpointHitInputDto) {
        endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitInputDto));
    }

    @Override
    public List<ViewStatsOutputDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, Boolean unique) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new BadRequestException("Даты не должны быть пустыми и start должен предшествовать end");
        }

        if (unique) {
            return endpointHitRepository.findDistinctIpStats(uris, start, end);
        } else {
            return endpointHitRepository.findStats(uris, start, end);
        }
    }

}
