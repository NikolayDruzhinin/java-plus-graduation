package ru.practicum.aggregator.config;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import java.util.Map;

public class ActionWeights { // веса не складываем, выставляем конкретное значение в границах от 0 до 1
    public static final Map<ActionTypeAvro, Double> WEIGHTS = Map.of(
            ActionTypeAvro.LIKE, 1.0,
            ActionTypeAvro.REGISTER, 0.8,
            ActionTypeAvro.VIEW, 0.4
    );
}

