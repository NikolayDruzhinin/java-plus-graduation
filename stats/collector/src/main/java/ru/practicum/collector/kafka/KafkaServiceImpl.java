package ru.practicum.collector.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.collector.mapper.CollectorMapper;
import ru.yandex.practicum.ewm.stats.proto.UserActionProto;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {
    private final KafkaProducer<String, SpecificRecordBase> producer;

    @Value("${kafka.topics.user-actions}")
    private String topicUserActions;

    @Override
    public void kafkaUserAction(UserActionProto userActionProto) {
        UserActionAvro avro = CollectorMapper.userActionToAvro(userActionProto);
        producer.send(new ProducerRecord<>(topicUserActions, avro));
    }
}
