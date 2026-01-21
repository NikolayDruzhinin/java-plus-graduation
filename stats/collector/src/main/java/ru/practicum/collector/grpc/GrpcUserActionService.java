package ru.practicum.collector.grpc;

import ru.yandex.practicum.ewm.stats.proto.UserActionProto;

public interface GrpcUserActionService {
    void processUserAction(UserActionProto userActionProto);
}

