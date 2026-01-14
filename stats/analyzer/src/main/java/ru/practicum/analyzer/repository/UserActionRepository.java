package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    UserAction findByEventIdAndUserId(Long eventId, Long userId);

    List<UserAction> findAllByUserId(Long userId);

    List<UserAction> findAllByEventIdIn(List<Long> eventIds);
}
