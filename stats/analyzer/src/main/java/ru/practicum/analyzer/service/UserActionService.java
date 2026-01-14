package ru.practicum.analyzer.service;

import ru.practicum.analyzer.model.UserAction;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionService {
    UserAction processUserAction(UserActionAvro userAction);

    UserAction findUserActionByEventIdAndUserId(Long eventId,  Long userId);
}
