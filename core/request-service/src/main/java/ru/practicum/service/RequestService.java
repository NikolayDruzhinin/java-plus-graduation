package ru.practicum.service;

import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);

    EventRequestStatusUpdateResult changeRequestStatus(Long initiatorId, Long eventId, EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getRequestByInitiator(Long userId, Long eventId);

}
