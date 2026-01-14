package ru.practicum.stats.analyzer.service;

import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.practicum.stats.analyzer.mapper.RecommendationsMapper;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.UserAction;
import ru.practicum.stats.analyzer.repository.SimilarityRepository;
import ru.practicum.stats.analyzer.repository.UserActionRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class RecommendationsService {
    final SimilarityRepository similarityRepository;
    final UserActionRepository actionRepository;

    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        List<Long> userRecentActionsEvents = actionRepository.findRecentEventIdsByUserId(request.getUserId(), request.getMaxResult());
        List<UserAction> allUserInteraction = actionRepository.findAllInteractionsByUser(request.getUserId());
        if (userRecentActionsEvents.isEmpty()) {
            return;
        }

        List<Long> similarities = similarityRepository.findSimilarUnseenEvents(
                        userRecentActionsEvents,
                        allUserInteraction.stream().map(UserAction::getEventId).toList(),
                        request.getMaxResult()
                ).stream().map(es -> userRecentActionsEvents.contains(es.getFirst()) ? es.getSecond() : es.getFirst())
                .toList();

        Map<Long, Double> eventScore = allUserInteraction.stream()
                .collect(Collectors.toMap(UserAction::getEventId, UserAction::getScore));

        List<RecommendedEventProto> result = new ArrayList<>();

        for (Long candidateId : similarities) {
            List<EventSimilarity> topK = similarityRepository
                    .findTopKSimilarUserEvents(candidateId,
                            allUserInteraction.stream().map(UserAction::getEventId).toList(), request.getMaxResult());

            double weightedSum = 0;
            double simSum = 0;
            for (EventSimilarity es : topK) {
                Long neighborId = es.getFirst().equals(candidateId) ? es.getSecond() : es.getFirst();
                Double neighborScore = eventScore.get(neighborId);
                if (neighborScore != null) {
                    weightedSum += neighborScore * es.getScore();
                    simSum += es.getScore();
                }
            }

            double predictedScore = simSum > 0 ? weightedSum / simSum : 0;

            result.add(RecommendedEventProto.newBuilder().setEventId(candidateId).setScore(predictedScore).build());
        }
        result = result.stream()
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .limit(request.getMaxResult())
                .toList();

        for (RecommendedEventProto proto : result) {
            responseObserver.onNext(proto);
        }
    }

    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEventProto> eventsSimilarity = similarityRepository.findByEventIdForUser(request.getEventId(),
                        request.getUserId(), request.getMaxResult()).stream()
                .map(e -> RecommendationsMapper.mapSimilarityToRecommendation(
                        e.getFirst() == request.getEventId() ? e.getSecond() : e.getFirst(), e.getScore()
                )).toList();
        for (RecommendedEventProto proto : eventsSimilarity) {
            responseObserver.onNext(proto);
        }
    }

    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        List<RecommendedEventProto> eventInteractions = actionRepository.findInteractions(request.getEventIdList())
                .stream().map(
                        e -> RecommendedEventProto.newBuilder().setEventId((long) e[0]).setScore((double) e[1]).build()
                ).toList();
        for (RecommendedEventProto proto : eventInteractions) {
            responseObserver.onNext(proto);
        }
    }
}
