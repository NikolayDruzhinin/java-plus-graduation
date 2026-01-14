package ru.practicum.stats.analyzer.mapper;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.EventSimilarityId;

public class SimilarityMapper {
    public static EventSimilarity mapAvroToEntity(EventSimilarityAvro avro) {
        return new EventSimilarity(avro.getEventA(), avro.getEventB(), avro.getScore());
    }

    public static EventSimilarityId mapAvroToKey(EventSimilarityAvro avro) {
        return new EventSimilarityId(avro.getEventA(), avro.getEventB());
    }
}
