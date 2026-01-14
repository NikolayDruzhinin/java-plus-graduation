package ru.practicum.application.event.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.*;
import ru.practicum.application.api.dto.event.EventFullDto;
import ru.practicum.application.api.dto.event.EventShortDto;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;

import java.util.List;

public interface EventInterface {
    @GetMapping("/events/{id}")
    EventFullDto getEventById(
            @PathVariable Long id,
            @RequestHeader("X-EWM-USER-ID") Long userId,
            HttpServletRequest request
    ) throws NotFoundException;

    @GetMapping("/events")
    List<EventShortDto> getFilteredEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean available,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer count,
            HttpServletRequest request
    ) throws ValidationException;

    @GetMapping("/events/recommendation")
    List<EventFullDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId);

    @PutMapping("/events/{eventId}/like")
    void likeEvent(@PathVariable Long eventId, @RequestHeader("X-EWM-USER-ID") Long userId) throws ValidationException;
}
