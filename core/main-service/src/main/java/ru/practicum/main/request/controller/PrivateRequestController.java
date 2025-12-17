package ru.practicum.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        log.info("PRIVATE get requests of user={}", userId);
        List<ParticipationRequestDto> newList = requestService.getUserRequests(userId);
        log.debug("SUCCESS: GET get requests of user={}, list of requests={}", userId, newList);
        return newList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable Long userId,
                                                           @RequestParam Long eventId) {
        log.info("PRIVATE add request user={} event={}", userId, eventId);
        ParticipationRequestDto newDto = requestService.addRequest(userId, eventId);
        log.debug("SUCCESS: POST add request user={} request={}", userId, newDto);
        return newDto;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("PRIVATE cancel request={} by user={}", requestId, userId);
        ParticipationRequestDto newDto = requestService.cancelRequest(userId, requestId);
        log.debug("SUCCESS: PATCH cancel request={} by user={}", newDto, userId);
        return newDto;
    }
}

