package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.dto.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    private static final long HOURS_BEFORE_EVENT = 2;

    public List<EventShortDto> getAllEventsOfUser(Long userId, int from, int size) {
        checkUserExists(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageRequest)
                .map(EventMapper::toEventShortDto)
                .getContent();
    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User initiator = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(dto.getCategory());

        validateEventDate(dto.getEventDate());

        Event event = EventMapper.toEvent(dto, initiator, category);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        Event event = getEventWithOwnerCheck(userId, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = getEventWithOwnerCheck(userId, eventId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new EventUpdateConflictException("Cannot modify published event");
        }

        if (dto.getEventDate() != null) {
            validateEventDate(dto.getEventDate());
        }

        Category category = dto.getCategory() != null ?
                getCategoryOrThrow(dto.getCategory()) : null;

        EventMapper.updateEventFromUserRequest(event, dto, category);

        if (dto.getStateAction() != null) {
            updateEventState(event, dto.getStateAction());
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private Event getEventWithOwnerCheck(Long userId, Long eventId) {
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " doesn't belong to user id=" + userId);
        }
        return event;
    }

    private void updateEventState(Event event, String stateAction) {
        switch (stateAction) {
            case "CANCEL_REVIEW":
                if (event.getState() != EventState.PENDING) {
                    throw new EventStateConflictException("Only pending events can be cancelled");
                }
                event.setState(EventState.CANCELED);
                break;
            case "SEND_TO_REVIEW":
                if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
                    throw new EventStateConflictException("Only pending or cancelled events can be sent for review");
                }
                event.setState(EventState.PENDING);
                break;
            default:
                throw new ValidationException("Invalid state action: " + stateAction);
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_EVENT))) {
            throw new EventDateConflictException(
                    "Event date must be at least " + HOURS_BEFORE_EVENT + " hours after current time"
            );
        }
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
    }

    private Event getEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " not found"));
    }

    private Category getCategoryOrThrow(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " not found"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Long getConfirmedRequests(Long eventId) {
        return requestRepository.countConfirmedRequestsByEventId(eventId);
    }

    private Long getViews(Long eventId) {
        return requestRepository.getViewsForEvent(eventId);
    }
}