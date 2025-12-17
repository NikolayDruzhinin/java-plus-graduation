package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.entity.Event;
import ru.practicum.main.event.entity.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.entity.ParticipationRequest;
import ru.practicum.main.request.entity.RequestStatus;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.repository.ParticipationRequestRepository;
import ru.practicum.main.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    // ----- user side -----
    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userService.getUserById(userId);
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (Objects.equals(event.getInitiatorId(), userId)) {
            throw new ConflictException("Initiator cannot request to participate in his own event");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event must be published");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Duplicate request");
        }

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int limit = Optional.ofNullable(event.getParticipantLimit()).orElse(0);
        if (limit > 0 && confirmed >= limit) {
            throw new ConflictException("The participant limit has been reached");
        }

        RequestStatus status = RequestStatus.PENDING;
        if (limit == 0 || Boolean.FALSE.equals(event.getRequestModeration())) {
            status = RequestStatus.CONFIRMED;
        }

        ParticipationRequest saved = requestRepository.save(
                ParticipationRequest.builder()
                        .eventId(eventId)
                        .requesterId(userId)
                        .status(status)
                        .created(LocalDateTime.now())
                        .build()
        );
        return RequestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest r = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        r.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toDto(requestRepository.save(r));
    }

    // ----- event owner side -----
    @Override
    public List<ParticipationRequestDto> getEventRequestsByUserId(Long userId, Long eventId) {
        Event e = mustBeOwner(userId, eventId);
        return requestRepository.findByEventId(e.getId()).stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest body) {
        Event e = mustBeOwner(userId, eventId);

        List<Long> ids = body.getRequestIds();
        if (ids == null || ids.isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        int limit = Optional.ofNullable(e.getParticipantLimit()).orElse(0);
        if (limit == 0 || Boolean.FALSE.equals(e.getRequestModeration())) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        RequestStatus target;
        try {
            target = RequestStatus.valueOf(String.valueOf(body.getStatus()));
        } catch (Exception ex) {
            throw new ConflictException("Unknown status: " + body.getStatus());
        }
        if (target != RequestStatus.CONFIRMED && target != RequestStatus.REJECTED) {
            throw new ConflictException("Unknown status: " + body.getStatus());
        }

        List<ParticipationRequest> reqs =
                requestRepository.findByIdInAndEventId(ids, eventId);

        if (reqs.size() != ids.size()
                || reqs.stream().anyMatch(r -> r.getStatus() != RequestStatus.PENDING)) {
            throw new ConflictException("Request must have status PENDING");
        }

        long alreadyConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        List<ParticipationRequest> toConfirm = new ArrayList<>();
        List<ParticipationRequest> toReject = new ArrayList<>();

        if (target == RequestStatus.CONFIRMED) {
            int slots = Math.max(0, limit - (int) alreadyConfirmed);
            if (slots == 0) {
                throw new ConflictException("The participant limit has been reached");
            }

            for (int i = 0; i < reqs.size(); i++) {
                ParticipationRequest r = reqs.get(i);
                if (i < slots) {
                    r.setStatus(RequestStatus.CONFIRMED);
                    toConfirm.add(r);
                } else {
                    r.setStatus(RequestStatus.REJECTED);
                    toReject.add(r);
                }
            }
            requestRepository.saveAll(reqs);

            if (alreadyConfirmed + toConfirm.size() >= limit) {
                requestRepository.rejectAllPendingForEvent(eventId, RequestStatus.REJECTED);
            }
        } else {
            reqs.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            requestRepository.saveAll(reqs);
            toReject.addAll(reqs);
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(toConfirm.stream().map(RequestMapper::toDto).collect(Collectors.toList()))
                .rejectedRequests(toReject.stream().map(RequestMapper::toDto).collect(Collectors.toList()))
                .build();
    }

    private Event mustBeOwner(Long userId, Long eventId) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!Objects.equals(e.getInitiatorId(), userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        return e;
    }
}