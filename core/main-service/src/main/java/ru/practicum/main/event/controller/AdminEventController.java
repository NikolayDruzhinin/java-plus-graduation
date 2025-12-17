package ru.practicum.main.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchAdminEvents(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) String rangeStart,
                                                @RequestParam(required = false) String rangeEnd,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("ADMIN search events users={} states={} cats={} from={} size={}", users, states, categories, from, size);
        List<EventFullDto> newList = eventService.searchAdminEvents(users, states, categories,
                rangeStart, rangeEnd, from, size);
        log.debug("SUCCESS: ADMIN search events newList={}", newList);
        return newList;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByAdmin(@PathVariable Long eventId,
                                      @Valid @RequestBody UpdateEventAdminRequest body) {
        log.info("ADMIN patch event={}", eventId);
        EventFullDto newDto = eventService.updateByAdmin(eventId, body);
        log.debug("SUCCESS: ADMIN patch event={}", newDto);
        return newDto;
    }
}

