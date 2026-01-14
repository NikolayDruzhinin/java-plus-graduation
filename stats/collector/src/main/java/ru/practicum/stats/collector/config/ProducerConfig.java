package ru.practicum.stats.collector.config;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.collector.kafka.ActionProducer;
import ru.practicum.stats.collector.kafka.UserActionSerializer;

import java.util.Properties;

@ConfigurationProperties("kafka.constants")
@AllArgsConstructor
public class ProducerConfig {
    private final String url;
    private final String topic;

    @Bean
    public ActionProducer producerConfig() {
        Properties properties = new Properties();
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserActionSerializer.class);

        Producer<String, UserActionAvro> producer = new KafkaProducer<>(properties);

        return new ActionProducer(producer, topic);
    }
}
