package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import feign.FeignException;

import java.util.List;

@FeignClient(name = "request-service", path = "/internal/api/users", contextId = "requestServiceClient")
public interface RequestClient {

    @GetMapping("/{userId}/requests/{eventId}")
    ResponseEntity<List<ParticipationRequestDto>> getRequestsForUserEvent(@PathVariable Long userId, @PathVariable Long eventId) throws FeignException;

    @PatchMapping("/{userId}/requests/{eventId}/change")
    ResponseEntity<EventRequestStatusUpdateResult> changeRequestsStatus(@PathVariable Long userId,
                                                                        @PathVariable Long eventId,
                                                                        @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) throws FeignException;
}
