package ru.practicum.application.request.service;

import com.google.protobuf.Timestamp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.api.dto.enums.EventState;
import ru.practicum.application.api.dto.event.EventFullDto;
import ru.practicum.application.api.dto.request.EventRequestDto;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;
import ru.practicum.application.event.client.EventClient;
import ru.practicum.application.request.mapper.EventRequestMapper;
import ru.practicum.application.request.model.EventRequest;
import ru.practicum.application.request.repository.RequestRepository;
import ru.practicum.application.user.client.UserClient;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.stats.client.CollectorClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.application.api.dto.enums.EventRequestStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestServiceImpl implements EventRequestService {

    final RequestRepository requestRepository;
    final UserClient userClient;
    final EventClient eventClient;
    final CollectorClient collectorClient;

    final EventRequestMapper eventRequestMapper;

    @Override
    @Transactional
    public EventRequestDto addRequest(Long userId, Long eventId) throws ConflictException, NotFoundException {
        collectorClient.sendUserAction(createUserAction(eventId, userId, ActionTypeProto.ACTION_REGISTER, Instant.now()));
        UserDto user = userClient.getById(userId);
        EventFullDto event = getEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Создатель события не может подать заявку на участие");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие не опубликовано");
        }
        List<EventRequest> requests = getEventRequestsByEventId(event.getId());
        if (participationLimitIsFull(event)) {
            throw new ConflictException("Превышен лимит заявок на участие в событии");
        }
        for (EventRequest request : requests) {
            if (request.getRequester().equals(userId)) {
                throw new ConflictException("Повторная заявка на участие в событии");
            }
        }

        EventRequest newRequest = createNewEventRequest(user, event);
        EventRequest eventRequest = requestRepository.save(newRequest);
        return eventRequestMapper.mapRequest(eventRequest);
    }

    @Override
    public List<EventRequestDto> getUserRequests(Long userId) throws NotFoundException {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден userId=" + userId);
        }
        return requestRepository.findByUserId(userId).stream()
                .map(eventRequestMapper::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventRequestDto> getRequestsByEventId(Long userId, Long eventId) throws ValidationException, NotFoundException {
        List<EventRequest> requests = getEventRequests(userId, eventId);
        return requests.stream()
                .map(eventRequestMapper::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestDto updateRequest(Long userId,
                                         Long eventId,
                                         EventRequestDto updateRequest) throws ConflictException, ValidationException, NotFoundException {
        EventFullDto event = getEventById(eventId);
        List<EventRequest> requests = getEventRequestsByEventId(eventId);
        long confirmedRequestsCounter = requests.stream().filter(r -> r.getStatus().equals(CONFIRMED_REQUEST)).count();

        List<EventRequestDto> confirmedRequests = new ArrayList<>();
        List<EventRequestDto> rejectedRequests = new ArrayList<>();

        List<EventRequest> result = new ArrayList<>();

        List<EventRequest> pending = requests.stream()
                .filter(p -> p.getStatus().equals(PENDING_REQUEST)).collect(Collectors.toList());


        for (EventRequest request : requests) {
            if (request.getStatus().equals(CONFIRMED_REQUEST) ||
                    request.getStatus().equals(REJECTED_REQUEST) ||
                    request.getStatus().equals(PENDING_REQUEST)) {

                if (updateRequest.getStatus().equals(CONFIRMED_REQUEST) && event.getParticipantLimit() != 0) {
                    if (event.getParticipantLimit() < confirmedRequestsCounter) {

                        pending.stream().peek(p -> p.setStatus(REJECTED_REQUEST)).collect(Collectors.toList());

                        throw new ConflictException("Превышено число возможных заявок на участие");
                    }
                }

                if (updateRequest.getStatus().equals(REJECTED_REQUEST) && request.getStatus().equals(CONFIRMED_REQUEST)) {
                    throw new ConflictException("Нельзя отменить подтверждённую заявку");
                }

                request.setStatus(updateRequest.getStatus());
                EventRequestDto participationRequestDto = eventRequestMapper.mapRequest(request);

                if (CONFIRMED_REQUEST.equals(participationRequestDto.getStatus())) {
                    confirmedRequests.add(participationRequestDto);
                } else if (REJECTED_REQUEST.equals(participationRequestDto.getStatus())) {
                    rejectedRequests.add(participationRequestDto);
                }

                result.add(request);
                confirmedRequestsCounter++;

            } else {
                throw new ValidationException("Неверный статус заявки");
            }
        }

        requestRepository.saveAll(pending);

        requestRepository.saveAll(result);

        return eventRequestMapper.mapRequestWithConfirmedAndRejected(confirmedRequests, rejectedRequests);
    }

    @Override
    @Transactional
    public EventRequestDto cancelRequest(Long userId, Long requestId) throws NotFoundException, ValidationException {

        if (!userClient.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден userId=" + userId);
        }

        EventRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос не существует")
        );
        if (!request.getRequester().equals(userId)) {
            throw new ValidationException("Создатель заявки не userId=" + userId);
        }
        request.setStatus(CANCELED_REQUEST);
        return eventRequestMapper.mapRequest(requestRepository.save(request));
    }

    @Override
    public Long countByEventAndStatuses(Long eventId, List<String> statuses) {
        return requestRepository.countByEventAndStatuses(eventId, statuses);
    }

    @Override
    public List<EventRequestDto> getByEventAndStatus(List<Long> eventId, String status) {
        return requestRepository.findByEventIdsAndStatus(eventId, status).stream()
                .map(eventRequestMapper::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventRequestDto> findByEventIds(List<Long> id) {
        return requestRepository.findByEventIds(id).stream()
                .map(eventRequestMapper::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserTakePart(Long userId, Long eventId) {
        return requestRepository.userTakePart(userId, eventId);
    }

    private EventRequest createNewEventRequest(UserDto user, EventFullDto event) {
        EventRequest newRequest = new EventRequest();
        newRequest.setRequester(user.getId());
        newRequest.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0) {
            newRequest.setStatus(CONFIRMED_REQUEST);
        } else {
            newRequest.setStatus(PENDING_REQUEST);
        }
        newRequest.setEvent(event.getId());
        if (!event.getRequestModeration()) {
            newRequest.setStatus(ACCEPTED_REQUEST);
        }
        return newRequest;
    }

    private boolean participationLimitIsFull(EventFullDto event) throws ConflictException {
        Long confirmedRequestsCounter = requestRepository.countByEventAndStatuses(event.getId(), List.of("CONFIRMED", "ACCEPTED"));
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequestsCounter) {
            throw new ConflictException("Превышено число заявок на участие");
        }
        return false;
    }

    private List<EventRequest> getEventRequests(Long userId, Long eventId) throws ValidationException, NotFoundException {
        UserDto user = userClient.getById(userId);
        EventFullDto event = getEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ValidationException("Пользователь не инициатор события c id=" + eventId);
        }
        return requestRepository.findByEventId(eventId);
    }

    private List<EventRequest> getEventRequestsByEventId(Long eventId) throws NotFoundException {
        if (eventClient.existsById(eventId)) {
            return requestRepository.findByEventId(eventId);
        } else {
            throw new NotFoundException("Событие не найдено eventId=" + eventId);
        }
    }

    private EventFullDto getEventById(Long eventId) throws NotFoundException {
        return eventClient.getInnerEventById(eventId);
    }

    private UserActionProto createUserAction(Long eventId, Long userId, ActionTypeProto type, Instant timestamp) {
        return UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(type)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
    }
}
