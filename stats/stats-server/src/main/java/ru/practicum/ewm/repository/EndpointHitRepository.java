package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.ViewStatsOutputDto;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    // Получение статистики по посещениям (без учета уникальности ip)
    @Query("""
            SELECT new ru.practicum.ewm.ViewStatsOutputDto(eh.app, eh.uri, COUNT(eh.ip))
            FROM EndpointHit eh
            WHERE (:uris IS NULL OR eh.uri IN :uris)
            AND (COALESCE(:start, NULL) IS NULL OR eh.timestamp >= :start)
            AND (COALESCE(:end, NULL) IS NULL OR eh.timestamp <= :end)
            GROUP BY eh.app, eh.uri
            ORDER BY COUNT(eh.ip) DESC
            """)
    List<ViewStatsOutputDto> findStats(@Param("uris") List<String> uris,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    // Получение статистики по посещениям (учитываются только уникальные посещения по ip)
    @Query("""
            SELECT new ru.practicum.ewm.ViewStatsOutputDto(eh.app, eh.uri, COUNT(DISTINCT eh.ip))
            FROM EndpointHit eh
            WHERE (:uris IS NULL OR eh.uri IN :uris)
            AND (COALESCE(:start, NULL) IS NULL OR eh.timestamp >= :start)
            AND (COALESCE(:end, NULL) IS NULL OR eh.timestamp <= :end)
            GROUP BY eh.app, eh.uri
            ORDER BY COUNT(DISTINCT eh.ip) DESC
            """)
    List<ViewStatsOutputDto> findDistinctIpStats(@Param("uris") List<String> uris,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

}
