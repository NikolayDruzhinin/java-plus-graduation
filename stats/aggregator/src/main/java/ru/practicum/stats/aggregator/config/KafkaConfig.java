package ru.practicum.stats.aggregator.config;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.handler.UserActionHandler;
import ru.practicum.stats.aggregator.kafka.SimilarityProducer;
import ru.practicum.stats.aggregator.kafka.SimilaritySerializer;
import ru.practicum.stats.aggregator.kafka.UserActionAvroDeserializer;
import ru.practicum.stats.aggregator.starter.AggregatorStarter;

import java.util.List;
import java.util.Properties;

@ConfigurationProperties("kafka.constants")
@AllArgsConstructor
public class KafkaConfig {
    private final String url;
    private final String action;
    private final String similarity;

    @Bean
    public AggregatorStarter aggregatorStarter(UserActionHandler handler) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "action");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionAvroDeserializer.class);

        Consumer<String, UserActionAvro> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(List.of(action));

        return new AggregatorStarter(consumer, handler);
    }

    @Bean
    public SimilarityProducer snapshotProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SimilaritySerializer.class);

        Producer<String, EventSimilarityAvro> producer = new KafkaProducer<>(properties);

        return new SimilarityProducer(producer, similarity);
    }
}
