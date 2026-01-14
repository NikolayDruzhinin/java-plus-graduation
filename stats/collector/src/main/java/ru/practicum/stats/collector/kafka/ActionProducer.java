package ru.practicum.stats.collector.kafka;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class ActionProducer {
    Producer<String, UserActionAvro> producer;
    final String topic;

    public void send(UserActionAvro action) {
        log.info("Отправления действия пользователя {} для события {}", action.getUserId(), action.getEventId());
        log.debug("Действие: {}", action.getActionType());

        ProducerRecord<String, UserActionAvro> record = new ProducerRecord<>(topic, action);

        producer.send(record);
        producer.flush();

        log.info("Действие отправлено");
    }
}
