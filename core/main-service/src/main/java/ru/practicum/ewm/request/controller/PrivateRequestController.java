package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsOfUser(@PathVariable Long userId) {
        log.info("[GET] Запросы пользователя с ID {}", userId);
        List<ParticipationRequestDto> requests = requestService.getRequestsOfUser(userId);

        return ResponseEntity.ok(requests);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addRequest(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        log.info("[POST] Добавление запроса на участие. Пользователь: {}, Событие: {}", userId, eventId);
        ParticipationRequestDto request = requestService.addRequest(userId, eventId);

        return ResponseEntity.status(201).body(request);
    }


    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        log.info("[PATCH] Отмена запроса с ID {} для пользователя {}", userId, requestId);
        ParticipationRequestDto canceledRequest = requestService.cancelRequest(userId, requestId);
        return ResponseEntity.ok(canceledRequest);
    }


}
