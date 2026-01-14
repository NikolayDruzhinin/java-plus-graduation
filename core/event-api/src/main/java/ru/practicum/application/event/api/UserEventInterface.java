package ru.practicum.application.event.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.application.api.dto.event.EventFullDto;
import ru.practicum.application.api.dto.event.EventShortDto;
import ru.practicum.application.api.dto.event.NewEventDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;
import ru.practicum.application.api.exception.WrongDataException;
import ru.practicum.application.api.request.event.UpdateEventUserRequest;

import java.util.List;

public interface UserEventInterface {
    @GetMapping("/users/{userId}/events")
    List<EventShortDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer count
    ) throws NotFoundException;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/events")
    EventFullDto addEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto event
    ) throws ValidationException, WrongDataException, NotFoundException, ConflictException;

    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getUserEventById(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) throws ValidationException, NotFoundException;

    @PatchMapping("/users/{userId}/events/{eventId}")
    EventFullDto updateUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest event
    ) throws ValidationException, ConflictException, WrongDataException, NotFoundException;
}
