package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StatsRequestParams {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start; // Дата начала выборки

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end; // Дата окончания выборки

    private List<String> uris; // Список URI для фильтрации статистики

    private Boolean unique = false; // Флаг учета уникальных посещений (по IP)
}
