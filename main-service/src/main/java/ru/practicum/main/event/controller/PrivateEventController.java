package ru.practicum.main.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("PRIVATE get events of user={} from={} size={}", userId, from, size);
        List<EventShortDto> newList = eventService.getUserEvents(userId, from, size);
        log.debug("SUCCESS: PRIVATE get events of user={} newList={}", userId, newList);
        return newList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto body) {
        log.info("PRIVATE add event by user={}", userId);
        EventFullDto newDto = eventService.addEvent(userId, body);
        log.debug("SUCCESS: PRIVATE add event by user={} newDto={}", userId, newDto);
        return newDto;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("PRIVATE get event={} by user={}", eventId, userId);
        EventFullDto newDto = eventService.getEventByUser(userId, eventId);
        log.debug("SUCCESS: PRIVATE get event={} newDto={}", eventId, newDto);
        return newDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest body) {
        log.info("PRIVATE patch event={} by user={}", eventId, userId);
        EventFullDto newDto = eventService.updateUserEvent(userId, eventId, body);
        log.debug("SUCCESS: PRIVATE update event={} newDto={}", eventId, newDto);
        return newDto;
    }
}

