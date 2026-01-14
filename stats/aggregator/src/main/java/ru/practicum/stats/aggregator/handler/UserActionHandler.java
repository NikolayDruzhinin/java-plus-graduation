package ru.practicum.stats.aggregator.handler;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.exception.IncorrectActionTypeException;
import ru.practicum.stats.aggregator.kafka.SimilarityProducer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionHandler {
    final SimilarityProducer producer;
    // Внешний ключ — событие, внутренний — пользователь, значение — вес оценки пользователем события
    final Map<Long, Map<Long, Double>> usersFeedback;
    // Ключ — пара двух событий, значение — сумма оценок общих пользователей
    // За оценку одного пользователя берётся наименьшая его оценка из оценок двух событий из ключа
    final Map<EventPair, Double> eventsMinWeightSum;
    // Ключ — событие, значение — сумма всех оценок пользователей данного события
    final Map<Long, Double> eventWeightSum;
    // Ключ — пара двух событий, значение — значение сходства двух событий из ключа
    final Map<EventPair, Double> eventsSimilarity;
    // Ключ — событие, значение — квадратный корень суммы всех оценок пользователей данного события
    final Map<Long, Double> sqrtCache;


    @Autowired
    public UserActionHandler(SimilarityProducer producer) {
        this.producer = producer;
        usersFeedback = new HashMap<>();
        eventsMinWeightSum = new HashMap<>();
        eventWeightSum = new HashMap<>();
        eventsSimilarity = new HashMap<>();
        sqrtCache = new HashMap<>();
    }

    public void handle(UserActionAvro avro) throws IncorrectActionTypeException {
        Long userId = avro.getUserId();
        Long eventId = avro.getEventId();
        Double weight = convertActionToWeight(avro.getActionType());

        // Получаем оценки пользователей для в avro события, еслил их нет, создаём новую мапу
        Map<Long, Double> userRatings = usersFeedback.computeIfAbsent(eventId, k -> new HashMap<>());
        Double oldWeight = userRatings.getOrDefault(userId, 0.0);

        if (oldWeight < weight) {
            userRatings.put(userId, weight);
            determineSimilarity(eventId, userId, oldWeight, weight, avro.getTimestamp());
        }
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }

    private void determineSimilarity(Long eventId, Long userId, Double oldWeight, Double newWeight, Instant timestamp) {
        // Сумму всех оценок пользователей для события
        // Для получения новой суммы необходимо вычесть старую оценку пользователя и добавить новую
        // Оценки остальных пользователей не меняются
        double newSum = eventWeightSum.getOrDefault(eventId, 0.0) - oldWeight + newWeight;
        eventWeightSum.put(eventId, newSum);
        // Убираем из кэша старый квадратный корень от суммк оценок
        sqrtCache.remove(eventId);

        // Начинаем проверку каждого сохранённого события
        for (Map.Entry<Long, Map<Long, Double>> entry : usersFeedback.entrySet()) {
            Long otherEventId = entry.getKey();
            Map<Long, Double> feedback = entry.getValue();
            // Если пользователь не взаимодейсвтовал со вторым событием, то подобие всегда будет 0
            if (!feedback.containsKey(userId) || Objects.equals(otherEventId, eventId)) continue;

            double convergenceWeight = feedback.get(userId);
            EventPair pair = EventPair.of(eventId, otherEventId);

            // Общая сумма оценок обоих событий, также как и сумма оценок самого события, изменяется только
            // по дейсвтующему пользователю, остальных рассматривать смысла нет
            double oldMinSum = eventsMinWeightSum.getOrDefault(pair, 0.0);
            double newMinSum = oldMinSum - Math.min(oldWeight, convergenceWeight) + Math.min(newWeight, convergenceWeight);
            eventsMinWeightSum.put(pair, newMinSum);

            double similarity = calculateSimilarity(pair, newMinSum);

            eventsSimilarity.put(pair, similarity);

            EventSimilarityAvro message = EventSimilarityAvro.newBuilder()
                    .setEventA(pair.first())
                    .setEventB(pair.second())
                    .setScore(similarity)
                    .setTimestamp(timestamp)
                    .build();
            producer.sendMessage(message);
        }
    }

    private double calculateSimilarity(EventPair pair, double commonSum) {
        double sqrtA = getSqrtSum(pair.first());
        double sqrtB = getSqrtSum(pair.second());

        // Используем формулу косинусного сходства двух векторов для определения сходства событий
        double similarity = commonSum / (sqrtA * sqrtB);
        log.info("Определено сходство событий {} и {}: {}", pair.first(), pair.second(), similarity);
        return similarity;
    }

    private double getSqrtSum(Long eventId) {
        return sqrtCache.computeIfAbsent(eventId, id -> Math.sqrt(eventWeightSum.getOrDefault(id, 0.0)));
    }

    private Double convertActionToWeight(ActionTypeAvro action) throws IncorrectActionTypeException {
        switch (action) {
            case VIEW -> {
                return 0.4;
            }
            case REGISTER -> {
                return 0.8;
            }
            case LIKE -> {
                return 1.0;
            }
            default -> {
                log.warn("Неверный тип действия пользователя: {}", action);
                throw new IncorrectActionTypeException("Неверный тип действия пользователя: " + action);
            }
        }
    }

    record EventPair(Long first, Long second) {
        public static EventPair of(Long a, Long b) {
            return a < b ? new EventPair(a, b) : new EventPair(b, a);
        }
    }
}
