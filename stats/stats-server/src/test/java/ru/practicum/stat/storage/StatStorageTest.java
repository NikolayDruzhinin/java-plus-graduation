package ru.practicum.stat.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import(StatStorageImpl.class)
public class StatStorageTest {
    @Autowired
    private StatStorageImpl statStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDb() {
        jdbcTemplate.update("DELETE FROM endpoint_hit");
        jdbcTemplate.update("ALTER TABLE endpoint_hit ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void saveHit_ShouldReturnId() {
        EndpointHit hit = new EndpointHit("ewm-main", "/events/1", "192.168.1.1", LocalDateTime.now());
        long id = statStorage.saveHit(hit);
        assertEquals(1L, id);
    }

    @Test
    void getStats_ShouldReturnStat_WhenUnique() {
        LocalDateTime now = LocalDateTime.now();
        EndpointHit hit1 = new EndpointHit("ewm-main", "/events/1", "192.168.1.1", LocalDateTime.now());
        EndpointHit hit2 = new EndpointHit("ewm-main", "/events/1", "192.168.1.2", LocalDateTime.now());

        statStorage.saveHit(hit1);
        statStorage.saveHit(hit2);

        List<ViewStats> stats = statStorage.getStats(
                now.minusDays(1), now.plusDays(1), List.of("/events/1"), true
        );
        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getHits()).isEqualTo(2); // 2 уникальных IP
    }
}
