package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.analyzer.model.EventSimilarity;

import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    @Query(""" 
        SELECT e 
        FROM EventSimilarity e 
        WHERE (e.eventA = :eventId OR e.eventB = :eventId) 
        AND (e.eventA NOT IN :excludedIds AND e.eventB NOT IN :excludedIds)""")
    List<EventSimilarity> findAllByEventIdAndEventIds(@Param("eventId") Long eventId,
                                                      @Param("excludedIds") List<Long> excludedIds);

    @Query("""
    SELECT e 
    FROM EventSimilarity e
    WHERE (e.eventA IN :eventIds OR e.eventB IN :eventIds)
      AND e.eventA NOT IN :excludedIds
      AND e.eventB NOT IN :excludedIds""")
    List<EventSimilarity> findAllByEventIdsAndExcludedIds(@Param("eventIds") List<Long> eventIds,
                                                          @Param("excludedIds") List<Long> excludedIds);

    List<EventSimilarity> findAllByEventA(Long eventAId);
}
