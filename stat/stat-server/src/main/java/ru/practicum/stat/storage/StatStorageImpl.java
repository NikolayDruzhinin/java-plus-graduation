package ru.practicum.stat.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StatStorageImpl implements StatStorage {
    protected final JdbcTemplate jdbc;
    private static final String SAVE_HIT = "INSERT INTO endpoint_hit (app, uri, ip, timestamp) VALUES (?, ?, ?, ?);";
    private static final String GET_STATS = """
            SELECT app, uri,
                   CASE WHEN ? = TRUE THEN COUNT(DISTINCT ip) ELSE COUNT(*) END AS hits
            FROM endpoint_hit
            WHERE timestamp BETWEEN ? AND ? {uris_condition}
            GROUP BY app, uri
            ORDER BY hits DESC
            """;

    public long saveHit(EndpointHit hit) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(SAVE_HIT, new String[]{"id"});
            int idx = 1;
            ps.setObject(idx++, hit.getApp());
            ps.setObject(idx++, hit.getUri());
            ps.setObject(idx++, hit.getIp());
            ps.setObject(idx, hit.getTimestamp());
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            return id;
        }
        throw new InternalServerException("Error while saving data");
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String urisCondition = (uris != null && !uris.isEmpty())
                ? "AND uri IN (" + uris.stream().map(uri -> "?").collect(Collectors.joining(",")) + ")"
                : "";

        String sql = GET_STATS.replace("{uris_condition}", urisCondition);

        return jdbc.query(sql, ps -> {
            int paramIndex = 1;
            ps.setBoolean(paramIndex++, unique); // Параметр для unique

            ps.setObject(paramIndex++, start);
            ps.setObject(paramIndex++, end);

            if (uris != null && !uris.isEmpty()) {
                for (String uri : uris) {
                    ps.setString(paramIndex++, uri);
                }
            }
        }, (rs, rowNum) -> new ViewStats(
                rs.getString("app"),
                rs.getString("uri"),
                rs.getInt("hits")
        ));
    }
}
