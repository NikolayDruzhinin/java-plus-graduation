package ru.practicum.kafka.deserializer;

import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;

public class EventsSimilarityDeserializer extends BaseAvroDeserializer<EventsSimilarityAvro> {
    public EventsSimilarityDeserializer() {
        super(EventsSimilarityAvro.getClassSchema());
    }
}
