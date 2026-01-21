package ru.practicum.analyzer;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.repository.UserActionRepository;
import ru.practicum.analyzer.config.ActionWeights;
import ru.yandex.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.yandex.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.yandex.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.yandex.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.yandex.practicum.ewm.stats.proto.UserPredictionsRequestProto;

import java.util.*;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class RecommendationController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserActionRepository actionRepository;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        Long userId = request.getUserId();
        Long maxResult = request.getMaxResults();
        List<EventSimilarity> newEvents = findNewEventsForUser(userId, maxResult);

        if(newEvents.isEmpty()) {
            responseObserver.onCompleted();
            return;
        }

        List<RecommendedEventProto> recommendations = calculateScores(userId, newEvents, maxResult);
        recommendations.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    private List<EventSimilarity> findNewEventsForUser(Long userId, Long maxresult) {
        List<Long> recentEventIds = actionRepository.findAllByUserId(userId).stream()
                .sorted(Comparator.comparing(UserAction::getTimestamp).reversed())
                .limit(maxresult)
                .map(UserAction::getEventId)
                .toList();

        List<Long> allUserEventIds = actionRepository.findAllByUserId(userId).stream()
                .map(UserAction::getEventId)
                .toList();

        return eventSimilarityRepository
                .findAllByEventIdsAndExcludedIds(recentEventIds, allUserEventIds)
                .stream()
                .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                .limit(maxresult)
                .toList();
    }

    private List<RecommendedEventProto> calculateScores(Long userId, List<EventSimilarity> candidates, Long maxResults) {
        List<UserAction> userActions = actionRepository.findAllByUserId(userId);

        Map<Long, Double> eventRatings = userActions.stream()
                .collect(Collectors.groupingBy(
                        UserAction::getEventId,
                        Collectors.summingDouble(action ->
                                ActionWeights.WEIGHTS.getOrDefault(action.getActionType(), 0.0))
                ));

        return candidates.stream()
                .map(candidate -> {
                    List<EventSimilarity> neighbors = eventSimilarityRepository
                            .findAllByEventA(candidate.getEventA());

                    List<EventSimilarity> ratedNeighbors = neighbors.stream()
                            .filter(n -> eventRatings.containsKey(
                                    Objects.equals(n.getEventA(), candidate.getEventB()) ? n.getEventB() : n.getEventA()
                            ))
                            .toList();

                    double weightedSum = 0.0;
                    double similaritySum = 0.0;

                    for (EventSimilarity neighbor : ratedNeighbors) {
                        Long neighborId = Objects.equals(neighbor.getEventA(), candidate.getEventB())
                                ? neighbor.getEventB()
                                : neighbor.getEventA();

                        double rating = eventRatings.getOrDefault(neighborId, 0.0);
                        double sim = neighbor.getScore();

                        weightedSum += rating * sim;
                        similaritySum += sim;
                    }

                    double finalScore = similaritySum > 0 ? weightedSum / similaritySum : 0.0;

                    return RecommendedEventProto.newBuilder()
                            .setEventId(candidate.getEventB())
                            .setScore(finalScore)
                            .build();
                })
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .toList();
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        Long userId = request.getUserId();
        Long maxResult = request.getMaxResults();
        Long eventId = request.getEventId();
        List<UserAction> userActions = actionRepository.findAllByUserId(userId);
        List<Long> userEventIds = userActions.stream()
                .map(UserAction::getEventId)
                .toList();

        eventSimilarityRepository.findAllByEventIdAndEventIds(eventId, userEventIds).stream()
                .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                .limit(maxResult)
                .forEach(similarity -> {
                    RecommendedEventProto proto = RecommendedEventProto.newBuilder()
                            .setEventId(similarity.getEventA())
                            .setScore(similarity.getScore())
                            .build();
                    responseObserver.onNext(proto);
                });

        responseObserver.onCompleted();
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        List<Long> eventIds = request.getEventIdList();
        actionRepository.findAllByEventIdIn(eventIds).stream()
                .collect(Collectors.groupingBy(
                        UserAction::getEventId,
                        Collectors.summingDouble(action ->
                                ActionWeights.WEIGHTS.getOrDefault(action.getActionType(), 0.0)
                        )
                ))
                .entrySet().stream()
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build()
                )
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }
}
