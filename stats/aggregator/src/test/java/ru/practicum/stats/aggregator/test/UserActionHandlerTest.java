package ru.practicum.stats.aggregator.test;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.exception.IncorrectActionTypeException;
import ru.practicum.stats.aggregator.handler.UserActionHandler;
import ru.practicum.stats.aggregator.kafka.SimilarityProducer;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Import(UserActionHandler.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionHandlerTest {
    UserActionHandler handler;
    @Mock
    SimilarityProducer producer;

    @BeforeEach
    void start() {
        handler = new UserActionHandler(producer);
    }

    @Test
    void shouldCalculateValidSimilarity() throws IncorrectActionTypeException {
        UserActionAvro action11 = UserActionAvro.newBuilder()
                .setUserId(1)
                .setEventId(1)
                .setActionType(ActionTypeAvro.VIEW)
                .setTimestamp(Instant.now())
                .build();
        UserActionAvro action12 = UserActionAvro.newBuilder()
                .setUserId(2)
                .setEventId(1)
                .setActionType(ActionTypeAvro.LIKE)
                .setTimestamp(Instant.now())
                .build();
        UserActionAvro action21 = UserActionAvro.newBuilder()
                .setUserId(1)
                .setEventId(2)
                .setActionType(ActionTypeAvro.REGISTER)
                .setTimestamp(Instant.now())
                .build();
        UserActionAvro action23 = UserActionAvro.newBuilder()
                .setUserId(3)
                .setEventId(2)
                .setActionType(ActionTypeAvro.VIEW)
                .setTimestamp(Instant.now())
                .build();
        final EventSimilarityAvro[] avro = new EventSimilarityAvro[1];

        doAnswer(invocationOnMock -> {
            avro[0] = invocationOnMock.getArgument(0);
            return null;
        }).when(producer).sendMessage(any(EventSimilarityAvro.class));

        handler.handle(action11);
        handler.handle(action12);
        handler.handle(action23);
        handler.handle(action21);

        Double weight11 = convertActionToWeight(action11.getActionType());
        Double weight12 = convertActionToWeight(action12.getActionType());
        Double weight21 = convertActionToWeight(action21.getActionType());
        Double weight23 = convertActionToWeight(action23.getActionType());

        Double minSumOfEventRating = Math.min(weight11, weight21) + Math.min(weight12, 0.0 /*weight22*/) +
                Math.min(0.0 /*weight13*/, weight23);
        Double sqrtOfEvent1Rating = Math.sqrt(weight11 + weight12);
        Double sqrtOfEvent2Rating = Math.sqrt(weight21 + weight23);

        Double similarity = minSumOfEventRating / (sqrtOfEvent1Rating * sqrtOfEvent2Rating);

        assertEquals(action12.getEventId(), avro[0].getEventA());
        assertEquals(action21.getEventId(), avro[0].getEventB());
        assertEquals(similarity, avro[0].getScore());
    }

    private Double convertActionToWeight(ActionTypeAvro action) {
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
                return 0.0;
            }
        }
    }
}
