package ru.practicum.analyzer.config;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;

import java.util.Map;

public class ActionWeights {
    public static final Map<ActionTypeAvro, Double> WEIGHTS = Map.of(
            ActionTypeAvro.LIKE, 1.0,
            ActionTypeAvro.REGISTER, 0.8,
            ActionTypeAvro.VIEW, 0.4
    );
}
