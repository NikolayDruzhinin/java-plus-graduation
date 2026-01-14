package ru.practicum.analyzer.mapper;

import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.UserAction;

public class UniversalMapper {
    public static EventSimilarity similarityFromAvro(EventsSimilarityAvro avro) {
        return EventSimilarity.builder()
                .score(avro.getScore())
                .eventA(avro.getEventA())
                .eventB(avro.getEventB())
                .timestamp(avro.getTimestamp())
                .build();

    }

    public static UserAction actionFromAvro(UserActionAvro avro) {
        return UserAction.builder()
                .actionType(avro.getActionType())
                .userId(avro.getUserId())
                .eventId(avro.getEventId())
                .timestamp(avro.getTimestamp())
                .build();
    }
}
