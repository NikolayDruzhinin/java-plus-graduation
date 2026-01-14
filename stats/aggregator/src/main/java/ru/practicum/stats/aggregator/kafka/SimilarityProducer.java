package ru.practicum.stats.aggregator.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SimilarityProducer {
    final Producer<String, EventSimilarityAvro> producer;
    final String topic;

    public void sendMessage(EventSimilarityAvro eventAvro) {
        log.info("Отправление сходства событий {} и {} ", eventAvro.getEventA(), eventAvro.getEventB());
        log.debug("Отправление EventSimilarityAvro {}", eventAvro);

        ProducerRecord<String, EventSimilarityAvro> producerRecord = new ProducerRecord<>(topic, eventAvro);

        producer.send(producerRecord);
        producer.flush();

        log.info("Отправлено сходство событий {} и {} ", eventAvro.getEventA(), eventAvro.getEventB());
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }
}
