package ru.practicum.collector.mapper;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.proto.ActionTypeProto;
import ru.yandex.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

public class CollectorMapper {
    public static UserActionAvro userActionToAvro(UserActionProto userActionProto) {
        UserActionAvro.Builder builder = UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setTimestamp(toInstant(userActionProto.getTimestamp()))
                .setActionType(actionTypeToAvro(userActionProto.getActionType()));

        return builder.build();
    }

    public static ActionTypeAvro actionTypeToAvro(ActionTypeProto actionTypeProto) {
        return switch (actionTypeProto) {
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Unrecognized action type: " + actionTypeProto);
        };
    }

    public static Instant toInstant(com.google.protobuf.Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
