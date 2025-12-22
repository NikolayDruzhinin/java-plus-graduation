package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.RequestClient;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/users")
public class RequestClientController implements RequestClient {
    private final RequestService requestService;

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsForUserEvent(Long userId, Long eventId) {
        log.info("[GET] Запросы пользователя с ID {} по событию с ID {}", userId, eventId);
        List<ParticipationRequestDto> requests = requestService.getRequestsForUserEvent(userId, eventId);

        return ResponseEntity.ok(requests);
    }

    @Override
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("[PATCH] Изменение статуса запроса пользователя с ID {} для события с ID {} с телом {}", userId, eventId, statusUpdateRequest);
        EventRequestStatusUpdateResult response = requestService.changeRequestsStatus(userId, eventId, statusUpdateRequest);

        return ResponseEntity.ok(response);
    }
}
