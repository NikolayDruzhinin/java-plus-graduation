package ru.practicum.application.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.application.api.dto.request.EventRequestDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;
import ru.practicum.application.request.api.EventRequestInterface;
import ru.practicum.application.request.service.EventRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestController implements EventRequestInterface {

    final EventRequestService requestService;

    @Override
    public EventRequestDto addEventRequest(Long userId, Long eventId) throws ConflictException, NotFoundException {
        return requestService.addRequest(userId, eventId);
    }

    @Override
    public List<EventRequestDto> getUserRequests(Long userId) throws NotFoundException {
        return requestService.getUserRequests(userId);
    }

    @Override
    public List<EventRequestDto> getRequestsByEventId(Long userId, Long eventId)
            throws ValidationException, NotFoundException {
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @Override
    public EventRequestDto updateRequest(Long userId, Long eventId, EventRequestDto request)
            throws ValidationException, ConflictException, NotFoundException {
        return requestService.updateRequest(userId, eventId, request);
    }

    @Override
    public EventRequestDto cancelRequest(Long userId, Long requestId) throws ValidationException, NotFoundException {
        return requestService.cancelRequest(userId, requestId);
    }
}
