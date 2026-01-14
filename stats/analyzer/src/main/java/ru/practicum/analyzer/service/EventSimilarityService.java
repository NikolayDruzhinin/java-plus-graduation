package ru.practicum.analyzer.service;

import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;

public interface EventSimilarityService {
    EventSimilarity processEventSimilarity(EventsSimilarityAvro eventSimilarityAvro);
}
