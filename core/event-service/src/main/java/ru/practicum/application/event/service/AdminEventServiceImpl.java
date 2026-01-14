package ru.practicum.application.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.api.dto.enums.EventState;
import ru.practicum.application.api.dto.enums.StateAction;
import ru.practicum.application.api.dto.event.EventFullDto;
import ru.practicum.application.api.dto.request.EventRequestDto;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;
import ru.practicum.application.api.exception.WrongDataException;
import ru.practicum.application.api.request.event.UpdateEventAdminRequest;
import ru.practicum.application.category.client.CategoryClient;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.application.event.repository.LocationRepository;
import ru.practicum.application.request.client.EventRequestClient;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.user.client.UserClient;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.stats.client.AnalyzerClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.application.api.util.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventServiceImpl implements AdminEventService {
    final EventRepository eventRepository;
    final LocationRepository locationRepository;

    final UserClient userClient;
    final CategoryClient categoryClient;
    final EventRequestClient requestClient;
    final AnalyzerClient analyzerClient;

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states,
                                        List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size)
            throws ValidationException {

        validateDateRange(rangeStart, rangeEnd);
        List<EventState> eventStates = parseEventStates(states);

        Map<Long, Event> eventsMap = findEvents(users, eventStates, categories,
                rangeStart, rangeEnd, from, size);

        if (eventsMap.isEmpty()) {
            return Collections.emptyList();
        }

        return buildEventFullDtos(eventsMap);
    }

    private void validateDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd)
            throws ValidationException {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Время начала поиска позже времени конца поиска");
        }
    }

    private List<EventState> parseEventStates(List<String> states) {
        return (states == null || states.isEmpty())
                ? Arrays.asList(EventState.values())
                : states.stream().map(EventState::valueOf).collect(Collectors.toList());
    }

    private Map<Long, Event> findEvents(List<Long> users, List<EventState> states,
                                        List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {

        if (users == null && categories == null) {
            return eventRepository.findAll(PageRequest.of(from / size, size))
                    .getContent().stream()
                    .collect(Collectors.toMap(Event::getId, Function.identity()));
        }

        return eventRepository.findAllEventsWithDates(
                users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate"))
        ).stream().collect(Collectors.toMap(Event::getId, Function.identity()));
    }

    private List<EventFullDto> buildEventFullDtos(Map<Long, Event> eventsMap) {
        List<Long> eventIds = new ArrayList<>(eventsMap.keySet());
        List<Long> userIds = extractUserIds(eventsMap);
        Set<Long> categoryIds = extractCategoryIds(eventsMap);

        Map<Long, UserDto> users = fetchUsers(userIds);
        Map<Long, CategoryDto> categories = fetchCategories(categoryIds);
        Map<Long, Long> confirmedRequests = countConfirmedRequests(eventIds);
        Map<Long, Double> eventRatings = fetchEventRatings(eventIds);

        return eventsMap.values().stream()
                .map(event -> buildEventFullDto(
                        event,
                        users.get(event.getInitiator()),
                        categories.get(event.getCategory()),
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        eventRatings.getOrDefault(event.getId(), 0.0)
                ))
                .collect(Collectors.toList());
    }

    private List<Long> extractUserIds(Map<Long, Event> eventsMap) {
        return eventsMap.values().stream()
                .map(Event::getInitiator)
                .distinct()
                .collect(Collectors.toList());
    }

    private Set<Long> extractCategoryIds(Map<Long, Event> eventsMap) {
        return eventsMap.values().stream()
                .map(Event::getCategory)
                .collect(Collectors.toSet());
    }

    private Map<Long, UserDto> fetchUsers(List<Long> userIds) {
        return userClient.getUsersList(userIds, 0, userIds.size()).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
    }

    private Map<Long, CategoryDto> fetchCategories(Set<Long> categoryIds) {
        return categoryClient.getCategoriesByIds(categoryIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));
    }

    private Map<Long, Long> countConfirmedRequests(List<Long> eventIds) {
        return requestClient.getByEventAndStatus(eventIds, "CONFIRMED").stream()
                .collect(Collectors.groupingBy(
                        EventRequestDto::getEvent,
                        Collectors.counting()
                ));
    }

    private Map<Long, Double> fetchEventRatings(List<Long> eventIds) {
        return analyzerClient.getInteractionsCount(getInteractionsRequest(eventIds)).stream()
                .collect(Collectors.toMap(
                        RecommendedEventProto::getEventId,
                        RecommendedEventProto::getScore
                ));
    }

    private EventFullDto buildEventFullDto(Event event, UserDto user,
                                           CategoryDto category, Long confirmedRequests,
                                           Double rating) {
        EventFullDto dto = EventMapper.mapEventToFullDto(
                event, confirmedRequests, category, user);
        dto.setRating(rating);
        return dto;
    }


    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) throws ConflictException, ValidationException, NotFoundException, WrongDataException {
        log.info("Редактирование данных события и его статуса");
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие не существует " + eventId));

        if (LocalDateTime.now().isAfter(event.getEventDate().minus(2, ChronoUnit.HOURS))) {
            throw new ConflictException("До начала события меньше часа, изменение события невозможно");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Событие не в состоянии \"Ожидание публикации\", изменение события невозможно");
        }
        if ((!StateAction.REJECT_EVENT.toString().equals(updateRequest.getStateAction())
                && event.getState().equals(EventState.PUBLISHED))) {
            throw new ConflictException("Отклонить опубликованное событие невозможно");
        }
        updateEventWithAdminRequest(event, updateRequest);
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Событие уже завершилось");
        }
        saveLocation(event);
        event = eventRepository.save(event);
        EventFullDto eventFullDto = getEventFullDto(event);
        return getViewsCounter(eventFullDto);
    }

    EventFullDto getEventFullDto(Event event) throws NotFoundException {
        Long confirmed = requestClient.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));
        return EventMapper.mapEventToFullDto(event, confirmed, categoryClient.getCategoryById(event.getCategory()),
                userClient.getById(event.getInitiator()));
    }

    Event getEventById(Long eventId) throws NotFoundException {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие " + eventId + " не найдено"));
    }

    void updateEventWithAdminRequest(Event event, UpdateEventAdminRequest updateRequest) throws NotFoundException, WrongDataException {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            if (!categoryClient.existById(updateRequest.getCategory())) {
                throw new NotFoundException("Категория не найдена " + updateRequest.getCategory());
            }
            event.setCategory(updateRequest.getCategory());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME)));
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(EventMapper.mapDtoToLocation(updateRequest.getLocation()));
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction().toUpperCase()) {
                case "PUBLISH_EVENT":
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case "REJECT_EVENT":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new WrongDataException("Неверное состояние события, не удалось обновить");
            }
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
    }

    void saveLocation(Event event) {
        event.setLocation(locationRepository.save(event.getLocation()));
        log.info("Локация сохранена {}", event.getLocation().getId());
    }

    EventFullDto getViewsCounter(EventFullDto eventFullDto) {
        List<RecommendedEventProto> protos = analyzerClient.getInteractionsCount(
                getInteractionsRequest(List.of(eventFullDto.getId()))
        );
        Double rating = protos.isEmpty() ? 0.0 : protos.getFirst().getScore();
        eventFullDto.setRating(rating);
        return eventFullDto;
    }

    private InteractionsCountRequestProto getInteractionsRequest(List<Long> eventId) {
        return InteractionsCountRequestProto.newBuilder().addAllEventId(eventId).build();
    }
}
