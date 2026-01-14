package ru.practicum.service;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.event.EventClient;
import ru.practicum.client.user.UserAdminClient;
import ru.practicum.StatsClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.enums.State;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.enums.Status;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ConflictPropertyConstraintException;
import ru.practicum.exception.ConflictRelationsConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;
import ru.yandex.practicum.ewm.stats.proto.ActionTypeProto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventClient eventClient;
    private final UserAdminClient userAdminClient;
    private final StatsClient statsClient;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        findUser(userId);
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addUserRequest(Long userId, Long eventId) {
        log.debug("Запрос дошёл до сервиса");
        UserDto user = findUser(userId);
        log.debug("Найден пользователь");
        EventFullDto event = findEvent(eventId, userId);
        log.debug("Найдено событие");
        requestRepository.findByRequesterIdAndEventId(userId, eventId).ifPresent(
                request -> {
                    throw new ConflictPropertyConstraintException("Нельзя добавить повторный запрос");
                }
        );

        if (Objects.equals(event.getInitiator().getId(), user.getId())) {
            throw new ConflictRelationsConstraintException(
                    "Инициатор события не может добавить запрос на участие в своём событии"
            );
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictRelationsConstraintException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getConfirmedRequests() == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictPropertyConstraintException("Достигнут лимит запросов на участие");
        }

        Status status = Status.PENDING;
        log.debug("Установлен статус PENDING");
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            log.debug("Установлен статус CONFIRMED");
            status = Status.CONFIRMED;
            log.debug("Вызван метод addConfirmedRequestToEvent");
            addConfirmedRequestToEvent(event);
            log.debug("Прошёл метод addConfirmedRequestToEvent");
        }

        Request request = Request.builder()
                .requesterId(user.getId())
                .status(status)
                .eventId(event.getId())
                .created(LocalDateTime.now().withNano(
                        (LocalDateTime.now().getNano() / 1_000_000) * 1_000_000
                        ))
                .build();
        log.debug("Отправляем статистику в statsClient");
        statsClient.sendUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
        log.debug("Статистика отправилась");

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }


    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        UserDto user = findUser(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос с id " + requestId + " не найден")
        );

        request.setStatus(Status.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long initiatorId, Long eventId, EventRequestStatusUpdateRequest httpRequest) {
        log.info("Начато обновление статусов запросов: инициаторId={}, eventId={}, целевой статус={}",
                initiatorId, eventId, httpRequest.getStatus());

        findUser(initiatorId);
        log.debug("Пользователь с id={} найден", initiatorId);

        EventFullDto event = findEvent(eventId, initiatorId);
        log.debug("Найдено событие: id={}, подтверждено участников={}, лимит участников={}",
                eventId, event.getConfirmedRequests(), event.getParticipantLimit());

        checkInitiatorEvent(initiatorId, eventId);
        log.debug("Проверка: пользователь {} является инициатором события {}", initiatorId, eventId);

        List<Request> requests = requestRepository.findAllByIdIn(httpRequest.getRequestIds());
        log.info("Найдено {} запрос(ов) для обновления: {}", requests.size(), httpRequest.getRequestIds());

        if (event.getConfirmedRequests() == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            log.warn("Лимит участников ({}) для события {} достигнут", event.getParticipantLimit(), eventId);
            throw new ConflictPropertyConstraintException("Достигнут лимит запросов на участие");
        }

        List<Long> invalidRequestByEventIds = requests.stream()
                .filter(request -> !Objects.equals(request.getEventId(), eventId))
                .map(Request::getId)
                .toList();

        if (!invalidRequestByEventIds.isEmpty()) {
            log.warn("Обнаружены запросы, не принадлежащие событию {}: {}", eventId, invalidRequestByEventIds);
            throw new ConflictPropertyConstraintException(
                    "Событие с id " + eventId + " не принадлежит запросам с id " + invalidRequestByEventIds
            );
        }

        List<Long> invalidRequestByStatusIds = requests.stream()
                .filter(request -> request.getStatus() != Status.PENDING)
                .map(Request::getId)
                .toList();

        if (!invalidRequestByStatusIds.isEmpty()) {
            log.warn("Обнаружены запросы с некорректным статусом (не PENDING): {}", invalidRequestByStatusIds);
            throw new ConflictPropertyConstraintException("Неверный статус у запросов c id " + invalidRequestByStatusIds);
        }

        if (httpRequest.getStatus().equals(Status.REJECTED)) {
            log.info("Все {} запрос(ов) будут отклонены", requests.size());

            List<Request> cancelRequests = requests.stream()
                    .peek(request -> request.setStatus(Status.REJECTED))
                    .toList();

            requestRepository.saveAll(cancelRequests);
            log.debug("Отклонённые запросы сохранены: {}", cancelRequests.stream().map(Request::getId).toList());

            List<ParticipationRequestDto> requestDTOs = requests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();

            log.info("Обновление завершено: {} запрос(ов) отклонено", requestDTOs.size());
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(List.of())
                    .rejectedRequests(requestDTOs)
                    .build();
        }

        long participantLimit = event.getParticipantLimit();
        int confirmedRequests = event.getConfirmedRequests();
        int requestsSize = requests.size();

        if (participantLimit - confirmedRequests < requestsSize) {
            log.warn("Недостаточно мест для подтверждения {} запросов: осталось {} из {}",
                    requestsSize, participantLimit - confirmedRequests, participantLimit);
            throw new ConflictPropertyConstraintException("Достигнут лимит запросов на участие");
        }

        log.info("Подтверждение {} запрос(ов)", requestsSize);
        List<Request> confirmedRequestsList = requests.stream()
                .peek(request -> request.setStatus(Status.CONFIRMED))
                .toList();
        requestRepository.saveAll(confirmedRequestsList);

        event.setConfirmedRequests(confirmedRequests + requestsSize);
        eventClient.saveFullEvent(event);
        log.debug("Событие {} обновлено: новое количество подтверждённых участников = {}", eventId, event.getConfirmedRequests());

        List<ParticipationRequestDto> confirmedRequestDTOs = confirmedRequestsList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();

        List<ParticipationRequestDto> canceledRequestDTOs = List.of();

        if (participantLimit - confirmedRequests == requestsSize) {
            log.info("Достигнут лимит участников для события {} — оставшиеся PENDING-запросы будут отклонены", eventId);

            List<Request> pendingRequests = requestRepository.findByEventIdAndStatus(eventId, Status.PENDING);
            List<Request> cancelRequests = pendingRequests.stream()
                    .peek(request -> request.setStatus(Status.REJECTED))
                    .toList();

            requestRepository.saveAll(cancelRequests);
            canceledRequestDTOs = cancelRequests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();

            log.debug("Отклонены PENDING-запросы: {}", cancelRequests.stream().map(Request::getId).toList());
        }

        log.info("Обновление завершено: подтверждено {} запрос(ов), отклонено {}",
                confirmedRequestDTOs.size(), canceledRequestDTOs.size());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequestDTOs)
                .rejectedRequests(canceledRequestDTOs)
                .build();
    }


    @Override
    public List<ParticipationRequestDto> getRequestByInitiator(Long userId, Long eventId) {
        checkInitiatorEvent(userId, eventId);
        return requestRepository.findByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    private UserDto findUser(Long userId) {
        try {
            return userAdminClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new ConflictException("Пользователь с id " + userId + " не найден");
        }
    }

    private EventFullDto findEvent(Long eventId, Long userId) {
        try {
            return eventClient.getEventById(eventId, userId);
        } catch (FeignException.NotFound e) {
            throw new ConflictException("Нельзя подать заявку: событие с id " + eventId + " не опубликовано");
        }
    }

    private void addConfirmedRequestToEvent(EventFullDto event) {
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventClient.saveFullEvent(event);
    }

    private void checkInitiatorEvent(Long initiatorId, Long eventId) {
        log.debug("Проверяем пользователя с id " + initiatorId + " на владение события с id " + eventId);
        EventFullDto check = eventClient.getEventByUserIdAndEventId(initiatorId, eventId);
        log.debug("Результат проверки: " + check);
        if (check == null || !Objects.equals(check.getInitiator().getId(), initiatorId)) {
            throw new NotFoundException(
                    "У пользователя с id " + initiatorId + " не найдено событие с id " + eventId
            );
        }
    }
}
