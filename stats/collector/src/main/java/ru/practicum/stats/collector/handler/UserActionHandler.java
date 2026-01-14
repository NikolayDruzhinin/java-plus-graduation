package ru.practicum.stats.collector.handler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.stats.collector.kafka.ActionProducer;

import java.time.Instant;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionHandler {
    final ActionProducer producer;

    public void handle(UserActionProto proto) {
        UserActionAvro actionAvro = UserActionAvro.newBuilder()
                .setUserId(proto.getUserId())
                .setEventId(proto.getEventId())
                .setActionType(changeToAvroAction(proto.getActionType()))
                .setTimestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .build();
        producer.send(actionAvro);
    }

    private ActionTypeAvro changeToAvroAction(ActionTypeProto action) {
        return ActionTypeAvro.valueOf(action.name().replace("ACTION_", ""));
    }
}
