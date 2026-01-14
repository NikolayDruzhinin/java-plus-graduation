package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.mapper.UniversalMapper;

@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {
    private final EventSimilarityRepository eventSimilarityRepository;

    public EventSimilarity processEventSimilarity(EventsSimilarityAvro eventSimilarityAvro) {
        EventSimilarity eventSimilarity = UniversalMapper.similarityFromAvro(eventSimilarityAvro);
        eventSimilarityRepository.save(eventSimilarity);
        return UniversalMapper.similarityFromAvro(eventSimilarityAvro);
    }
}
