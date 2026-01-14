package ru.practicum.stats.aggregator.kafka;

import org.apache.avro.Schema;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public class UserActionAvroDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public UserActionAvroDeserializer() {
        super(UserActionAvro.getClassSchema());
    }

    public UserActionAvroDeserializer(Schema schema) {
        super(schema);
    }
}
