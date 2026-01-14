package ru.practicum.application.event.service;

import ru.practicum.application.api.dto.event.EventFullDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;
import ru.practicum.application.api.exception.WrongDataException;
import ru.practicum.application.api.request.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) throws ValidationException;

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest event) throws ConflictException, ValidationException, NotFoundException, WrongDataException;

}
