package ru.practicum.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@Valid @RequestBody NewEventDto newEventDto,
                                  @PathVariable(name = "userId") Long userId) {
        log.info("Получен запрос на создание события от пользователя {}", userId);
        return eventService.saveEvent(newEventDto, userId);
    }

    @PutMapping("/events/{eventId}/like")
    public void likeEvent(@PathVariable Long eventId,
                          @RequestHeader(value = "X-EWM-USER-ID", required = false) Long userId) {
        log.info("GET /events/{}/like/ for user {}", eventId, userId);
        eventService.likeEvent(eventId, userId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByIdAndUserId(@RequestBody @Valid UpdatedEventDto updatedEventDto,
                                                 @PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        
        log.info("PATCH /users/{}/events/{}", userId, eventId);
        return eventService.updateEvent(updatedEventDto, userId, eventId);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateAdminEventById(@RequestBody @Valid UpdatedEventDto updatedEventDto,
                                                 @PathVariable Long eventId) {
        
        log.info("PATCH /admin/events/{} ", eventId);
        return eventService.updateAdminEvent(updatedEventDto, eventId);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId,
                                     @RequestHeader(value = "X-EWM-USER-ID", required = false) Long userId) {
        log.info("GET /events/{}, с userId={}", eventId, userId);
        return eventService.getEventById(eventId, userId);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("GET /event params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, sort, from, size);
        return eventService.getEvents(text, categories, paid,
                                        rangeStart, rangeEnd,
                                        onlyAvailable, sort, from, size,
                                        "user");
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsByUserId(@PathVariable Long userId,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        
        log.info("GET /users/{}/events, from={}, size={}", userId, from, size);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEventByUserIdAndEventId(@PathVariable("userId") Long userId,
                                                   @PathVariable("eventId") Long eventId) {
        
        log.info("GET /users/{}/events/{}, from={}, size={}", userId, eventId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getAdminEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        
        log.info("GET /admin/events, params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, sort, from, size);
        return eventService.getEvents(text, categories, paid,
                rangeStart, rangeEnd,
                onlyAvailable, sort, from, size,
                "admin");
    }

    @GetMapping("/events/recommendations")
    public List<EventShortDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId) {
        log.info("GET /events/recommendations for user {}", userId);
        return eventService.getRecommendations(userId);
    }

    @PostMapping("/admin/events/increment")
    public EventFullDto saveFullEvent(@RequestBody EventFullDto event) {
        return eventService.saveFullEvent(event);
    }
}