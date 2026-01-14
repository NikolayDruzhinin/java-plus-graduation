package ru.practicum.stats.analyzer.kafka;

import org.apache.avro.Schema;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public class SimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    public SimilarityDeserializer() {
        super(EventSimilarityAvro.getClassSchema());
    }
    public SimilarityDeserializer(Schema schema) {
        super(schema);
    }
}
