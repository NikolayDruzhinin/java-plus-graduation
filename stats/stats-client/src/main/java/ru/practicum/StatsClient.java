package ru.practicum;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.stats.proto.ActionTypeProto;
import ru.yandex.practicum.ewm.stats.proto.UserActionProto;
import ru.yandex.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.yandex.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.yandex.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.yandex.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.yandex.practicum.ewm.stats.proto.UserPredictionsRequestProto;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class StatsClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collector;

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzer;

    public Stream<RecommendedEventProto> getSimilarEvents (long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setMaxResults(maxResults)
                .build();

        Iterator<RecommendedEventProto> iterator = analyzer.getSimilarEvents(request);
        return asStream(iterator);
    }

    public Stream<RecommendedEventProto> getInteractionsCount (List<Long> eventId) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventId)
                .build();
        Iterator<RecommendedEventProto> iterator = analyzer.getInteractionsCount(request);

        return asStream(iterator);
    }

    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        Iterator<RecommendedEventProto> iterator = analyzer.getRecommendationsForUser(request);

        return asStream(iterator);
    }

    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    public void sendUserAction (long userId, long eventId, ActionTypeProto actionType) {
        log.debug("Получили запрос на отправку статистики от userId " + userId +
                ", строим запрос по eventId " + eventId);
        UserActionProto request = UserActionProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setTimestamp(getCurrentTimestamp())
                .setActionType(actionType)
                .build();
        log.debug("Построили запрос");
        log.debug("Отправляем запрос");
        collector.collectUserAction(request);
        log.debug("Отправили запрос");
    }

    private Timestamp getCurrentTimestamp() {
        return Timestamp.newBuilder()
                .setSeconds(Instant.now().getEpochSecond())
                .setNanos(Instant.now().getNano())
                .build();
    }
}
