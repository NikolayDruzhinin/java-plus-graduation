package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorStarter implements Runnable {
    private final AggregatorService aggregatorService;
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    @Value("${kafka.topics.user-actions}")
    private String userActionsTopic;
    @Value("${kafka.topics.events-similarity}")
    private String eventsSimilarityTopic;

    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(userActionsTopic));
            log.info("Подписка на топик " + userActionsTopic);

            while (running) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));
                log.trace("Получено {} событий", records.count());

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    log.debug("Получено сообщение: offset={}, partition={}, key={}, timestamp={}, value={}",
                            record.offset(), record.partition(), record.key(), record.timestamp(), record.value());

                    if(!(record.value() instanceof UserActionAvro userActionAvro)) {
                        log.warn("Неизвестное значение записи: {}", record.value().getClass().getSimpleName());
                        continue;
                    }

                    List<EventsSimilarityAvro> eventSimilarity = aggregatorService.updateSimilarity(userActionAvro);
                    eventSimilarity.forEach( similarity -> {
                                try {
                                    producer.send(new ProducerRecord<>(eventsSimilarityTopic, similarity));
                                    log.info("Обновлено  сходство {} для события: {}", similarity, similarity.getEventA());
                                } catch (Exception e) {
                                    log.error("Ошибка при отправке в кафка", e);
                                }
                            }
                    );

                    if (eventSimilarity.isEmpty()) {
                        log.info("Нечего отправлять.");
                    }

                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
            log.info("WakeupException: получен сигнал завершения");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                log.info("Завершаем: flush и commitSync");
                producer.flush();
                consumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка при завершении и commitSync", e);
            } finally {
                log.info("Закрываем продюсер и консьюмер");
                try {
                    producer.close();
                } catch (Exception e) {
                    log.error("Ошибка при закрытии продюсера", e);
                }
                try {
                    consumer.close();
                } catch (Exception e) {
                    log.error("Ошибка при закрытии консьюмера", e);
                }
            }
        }
    }
}
