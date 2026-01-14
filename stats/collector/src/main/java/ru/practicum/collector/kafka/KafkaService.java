package ru.practicum.collector.kafka;

import ru.yandex.practicum.ewm.stats.proto.UserActionProto;

public interface KafkaService {
    void kafkaUserAction(UserActionProto userActionProto);
}
