package ru.practicum.stats.analyzer.starter;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.service.UserActionService;

import java.time.Duration;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
public class UserActionStarter implements Runnable {
    final Consumer<String, UserActionAvro> consumer;
    final UserActionService service;

    public UserActionStarter(Consumer<String, UserActionAvro> consumer, UserActionService service) {
        this.consumer = consumer;
        this.service = service;
    }

    @Override
    public void run() {
        try {
            log.info("Получение данных");
            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, UserActionAvro> record : records) {
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
