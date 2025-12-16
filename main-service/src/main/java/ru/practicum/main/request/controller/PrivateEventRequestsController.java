package ru.practicum.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventRequestsController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getEventRequestsByUserId(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        log.info("PRIVATE get requests for event={} by owner={}", eventId, userId);
        List<ParticipationRequestDto> newList = requestService.getEventRequestsByUserId(userId, eventId);
        log.debug("SUCCESS: PRIVATE get requests for event={} by owner={} newList={}", eventId, userId, newList);
        return newList;
    }

    @PatchMapping
    public EventRequestStatusUpdateResult changeStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest body) {
        log.info("PRIVATE change status for event={} by owner={}, body={}", eventId, userId, body);
        return requestService.changeRequestStatus(userId, eventId, body);
    }
}
