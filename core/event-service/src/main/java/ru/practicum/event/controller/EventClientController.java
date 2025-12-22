package ru.practicum.event.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.EventClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.AdminEventService;
import ru.practicum.event.service.PublicEventService;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/events")
public class EventClientController implements EventClient {
    private final PublicEventService publicEventService;
    private final AdminEventService adminEventService;

    @Override
    public Optional<Event> getEventFullById(long id) throws FeignException {
        log.info("Поступил запрос Get /events/{}/full на получение Event model с id = {}", id, id);
        Optional<Event> response = publicEventService.getEventFullById(id);
        log.info("Сформирован ответ Get /events/{}/full с телом: {}", id, response);
        return response;
    }

    @Override
    public Event save(Event event) throws FeignException {
        log.info("Поступил запрос Post /admin/events на сохранение Event с телом = {}", event);
        Event response = adminEventService.saveEventFull(event);
        log.info("Сформирован ответ Post /admin/events с телом: {}", response);
        return response;
    }

}
