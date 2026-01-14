package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ParticipantLimitReachedException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.repository.RequestRepository;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.user.model.User;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventClient eventClient;
    private final UserClient userClient;


    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        getUser(userId);
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto)//toParticipationRequestDto
                .collect(Collectors.toList());
    }


    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEventOrThrow(eventId);

        System.out.println(event.toString());


        if (event.getInitiatorId().equals(userId)) {
            throw new ValidationException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии.");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidationException("Нельзя повторно подавать заявку на то же событие.");
        }

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ParticipantLimitReachedException("Лимит участников уже достигнут");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEventId(eventId);
        request.setRequesterId(userId);
/* Условие !event.isRequestModeration()- если для события не требуется премодерация заявок на участие, то заявка должна автоматически переходить в статус CONFIRMED.
"Если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного".
Условие event.getParticipantLimit() == 0 - если нет ограничения на количество участников (лимит равен 0), заявка тоже должна автоматически подтверждаться.
"Если для события лимит заявок равен 0, то подтверждение заявок не требуется".
Статусы заявок - если хотя бы одно из условий выполняется, заявка переходит в статус CONFIRMED. Если оба
условия не выполняются (например, премодерация включена и есть лимит участников), заявка остается в статусе PENDING.*/
        boolean autoConfirm = !event.isRequestModeration() || event.getParticipantLimit() == 0;
        request.setStatus(autoConfirm ? RequestStatus.CONFIRMED : RequestStatus.PENDING);
        ParticipationRequest savedRequest = requestRepository.save(request);
        //если заявка CONFIRMED, нужно увеличить счётчик confirmedRequests
        if (RequestStatus.CONFIRMED.equals(savedRequest.getStatus())) {
            updateConfirmedRequests(event.getId());
        }
        return RequestMapper.toParticipationRequestDto(savedRequest);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена или не принадлежит пользователю."));
        boolean wasConfirmed = RequestStatus.CONFIRMED.equals(request.getStatus());
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest updatedRequest = requestRepository.save(request);

        if (wasConfirmed) {
            updateConfirmedRequests(request.getEventId());
        }


        return RequestMapper.toParticipationRequestDto(updatedRequest);

    }

    public List<ParticipationRequestDto> getRequestsForUserEvent(Long userId, Long eventId) {
        getUser(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiatorId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }


    @Transactional
    public EventRequestStatusUpdateResult changeRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiatorId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }

        List<Long> requestIds = statusUpdateRequest.getRequestIds();

        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);

        for (ParticipationRequest request : requests) {

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ValidationException("Можно менять статус только у заявок в состоянии PENDING");
            }
            //проверяем лимит
            if (statusUpdateRequest.getStatus() == RequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new ParticipantLimitReachedException("Лимит участников уже достигнут");
                }

                request.setStatus(RequestStatus.CONFIRMED);

            } else {

                request.setStatus(statusUpdateRequest.getStatus());
            }
        }

        requestRepository.saveAll(requests);
        updateConfirmedRequests(eventId);
        List<ParticipationRequestDto> confirmedRequests = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.CONFIRMED)
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.REJECTED)
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Transactional
    public void updateConfirmedRequests(Long eventId) {
        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        confirmedRequests = (confirmedRequests == null) ? 0 : confirmedRequests;

        Event event = eventClient.getEventFullById(eventId).orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
        event.setConfirmedRequests(confirmedRequests);
        System.out.println("____________________" + event.toString());
        eventClient.save(event);
    }

    private User getUser(Long userId) {
        return userClient.getUserById(userId)
            .orElseThrow(() -> {
                log.error("Пользователь с id={} не найден", userId);
                return new NotFoundException("Пользователь с id=" + userId + " не найден");
            });
    }

    private Event getEventOrThrow(Long eventId) {
        return eventClient.getEventFullById(eventId)
                .orElseThrow(() -> {
                    log.error("Событие с id={} не найдено", eventId);
                    return new NotFoundException("Событие с id=" + eventId + " не найдено");
                });
    }


}
