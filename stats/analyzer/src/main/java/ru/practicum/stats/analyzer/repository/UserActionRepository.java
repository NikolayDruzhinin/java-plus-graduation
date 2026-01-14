package ru.practicum.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.analyzer.model.UserAction;
import ru.practicum.stats.analyzer.model.UserActionId;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, UserActionId> {
    @Query("""
            SELECT ua.eventId, SUM(ua.score)
            FROM UserAction ua
            WHERE ua.eventId IN :eventIds
            GROUP BY ua.eventId
            """)
    List<Object[]> findInteractions(@Param("eventIds") List<Long> eventIds);

    @Query("""
            SELECT ua.eventId
            FROM UserAction ua
            WHERE ua.userId = :userId
            ORDER BY ua.timestamp DESC
            LIMIT :limit
            """)
    List<Long> findRecentEventIdsByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    @Query("""
            SELECT ua
            FROM UserAction ua
            WHERE ua.userId = :userId
            """)
    List<UserAction> findAllInteractionsByUser(@Param("userId") Long userId);
}
