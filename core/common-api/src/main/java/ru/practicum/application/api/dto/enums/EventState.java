package ru.practicum.application.api.dto.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static List<String> getAll() {
        return Arrays.stream(values()).map(EventState::name).collect(Collectors.toList());
    }
}
