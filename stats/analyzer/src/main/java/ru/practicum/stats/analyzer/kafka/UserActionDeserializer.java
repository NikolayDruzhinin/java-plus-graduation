package ru.practicum.stats.analyzer.kafka;

import org.apache.avro.Schema;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public class UserActionDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public UserActionDeserializer() {
        super(UserActionAvro.getClassSchema());
    }

    public UserActionDeserializer(Schema schema) {
        super(schema);
    }
}
