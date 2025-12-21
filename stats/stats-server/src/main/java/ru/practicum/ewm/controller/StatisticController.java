package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.service.StatisticServiceImpl;
import ru.practicum.ewm.ViewStatsOutputDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatisticController {

    private final StatisticServiceImpl statisticServiceImpl;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEndpointHit(@Validated @RequestBody EndpointHitInputDto endpointHitInputDto) {
        log.info("Поступил запрос Post /hit на создание EndpointHit с телом: {}", endpointHitInputDto);
        statisticServiceImpl.createEndpointHit(endpointHitInputDto);
        log.info("Обработан запрос Post /hit на создание EndpointHit с телом: {}", endpointHitInputDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsOutputDto> getStats(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                             @RequestParam(required = false) List<String> uris,
                                             @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        log.info("Поступил запрос Get /stats на получение List<ViewStatsOutputDto> с параметрами: start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        List<ViewStatsOutputDto> response = statisticServiceImpl.getStats(uris, start, end, unique);
        log.info("Сформирован ответ Get /stats с телом: {}", response);

        return response;
    }
}
