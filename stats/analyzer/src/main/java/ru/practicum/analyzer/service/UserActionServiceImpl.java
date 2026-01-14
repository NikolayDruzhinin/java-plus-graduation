package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.config.ActionWeights;
import ru.practicum.analyzer.mapper.UniversalMapper;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.analyzer.repository.UserActionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserActionRepository userActionRepository;

    public UserAction processUserAction(UserActionAvro userAction) {
        UserAction userActionFromBD = findUserActionByEventIdAndUserId(userAction.getEventId(), userAction.getUserId());
        UserAction userActionFromAvro = UniversalMapper.actionFromAvro(userAction);
        if (userActionFromBD != null) {
            if (ActionWeights.WEIGHTS.get(userAction.getActionType()) >=
                    ActionWeights.WEIGHTS.get(userActionFromBD.getActionType())) {
                userActionFromBD.setActionType(userAction.getActionType());
                userActionFromBD.setTimestamp(userAction.getTimestamp());
                userActionFromBD = userActionRepository.save(userActionFromBD);
            }
        } else {
            userActionFromBD = userActionRepository.save(userActionFromAvro);
        }

        return userActionFromBD;
    }

    public UserAction findUserActionByEventIdAndUserId(Long eventId,  Long userId) {
        return userActionRepository.findByEventIdAndUserId(eventId, userId);
    }
}