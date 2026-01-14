package ru.practicum.stats.analyzer.starter;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.service.SimilarityService;

import java.time.Duration;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
public class SimilarityStarter implements Runnable {
    final Consumer<String, EventSimilarityAvro> consumer;
    final SimilarityService service;

    public SimilarityStarter(Consumer<String, EventSimilarityAvro> consumer, SimilarityService service) {
        this.consumer = consumer;
        this.service = service;
    }

    @Override
    public void run() {
        try {
            log.info("Получение данных");
            while (true) {
                ConsumerRecords<String, EventSimilarityAvro> records = consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    service.save(record.value());
                }
            }
        } catch (WakeupException e) {

        } catch (Exception e) {
            log.error("Сбой обработки ", e);
            log.error(e.getMessage());
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
            }
        }
    }
}
