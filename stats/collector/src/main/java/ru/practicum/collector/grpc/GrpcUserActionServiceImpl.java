package ru.practicum.collector.grpc;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import ru.practicum.collector.kafka.KafkaService;
import ru.yandex.practicum.ewm.stats.proto.UserActionProto;

@Service
@RequiredArgsConstructor
public class GrpcUserActionServiceImpl implements GrpcUserActionService {

    private final KafkaService kafkaService;

    @Override
    public void processUserAction(UserActionProto userActionProto) {
        kafkaService.kafkaUserAction(userActionProto);
    }
}