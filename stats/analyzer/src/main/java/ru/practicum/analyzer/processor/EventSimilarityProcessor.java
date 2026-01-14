package ru.practicum.analyzer.processor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.analyzer.service.EventSimilarityService;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.analyzer.model.EventSimilarity;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class EventSimilarityProcessor implements Runnable {

    @Autowired
    @Qualifier("kafkaConsumerEventSimilarity")
    private KafkaConsumer<String, SpecificRecordBase> consumer;
    @Autowired
    private EventSimilarityService eventSimilarityService;
    @Value("${kafka.topics.event-similarity}")
    private String eventSimilarityTopic;

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(eventSimilarityTopic));
            log.info("EventSimilarityProcessor подписан на топик {}", eventSimilarityTopic);

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                for(ConsumerRecord<String, SpecificRecordBase> record : records) {
                    if(!(record.value() instanceof EventsSimilarityAvro eventSimilarityAvro)) {
                        log.warn("Неожиданный тип записи: {}", record.value().getClass().getSimpleName());
                        continue;
                    }

                    EventSimilarity eventSimilarity = eventSimilarityService.processEventSimilarity(eventSimilarityAvro);
                    log.info("Сохранена схожесть событий: {}", eventSimilarity);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignore) {
            // Игнорируем
        } catch (Exception e) {
            log.error("Ошибка во время обработки схожести событий", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем consumer");
                consumer.close();
            }
        }
    }
}