package ru.practicum.application.event.service;

import com.google.protobuf.Timestamp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.api.dto.enums.EventState;
import ru.practicum.application.api.dto.event.EventFullDto;
import ru.practicum.application.api.dto.event.EventShortDto;
import ru.practicum.application.api.dto.request.EventRequestDto;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;
import ru.practicum.application.category.client.CategoryClient;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.application.request.client.EventRequestClient;
import ru.practicum.application.user.client.UserClient;
import ru.practicum.ewm.stats.proto.*;
import ru.practicum.stats.client.AnalyzerClient;
import ru.practicum.stats.client.CollectorClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.application.api.util.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {
    final EventRepository eventRepository;

    final UserClient userClient;
    final CategoryClient categoryClient;
    final EventRequestClient requestClient;
    final CollectorClient collectorClient;
    final AnalyzerClient analyzerClient;

    @Override
    public EventFullDto getEventById(Long eventId, Long userId, String uri, String ip) throws NotFoundException {
        collectorClient.sendUserAction(createUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW, Instant.now()));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (!event.getState().equals(EventState.PUBLISHED) && !uri.toLowerCase().contains("admin")) {
            throw new NotFoundException("Такого события не существует");
        }
        var confirmed = requestClient.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));
        EventFullDto eventFullDto = EventMapper.mapEventToFullDto(
                event,
                confirmed,
                categoryClient.getCategoryById(event.getCategory()),
                userClient.getById(event.getInitiator())
        );

        List<RecommendedEventProto> proto = analyzerClient.getInteractionsCount(
                getInteractionsRequest(eventId)
        );
        Double rating = proto.isEmpty() ? 0.0 : proto.getFirst().getScore();
        eventFullDto.setRating(rating);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getFilteredEvents(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 String sort,
                                                 Integer from,
                                                 Integer size,
                                                 String uri,
                                                 String ip) throws ValidationException {
        List<Event> events;
        LocalDateTime startDate;
        LocalDateTime endDate;
        boolean sortDate = sort.equals("EVENT_DATE");
        if (sortDate) {
            if (rangeStart == null && rangeEnd == null && categories != null) {
                //events = eventRepository.findAllByCategoryIdPageable(categories, EventState.PUBLISHED, PageRequest.of(from / size, size, Sort.Direction.DESC));
                events = eventRepository.findAllByCategoryIdPageable(categories, EventState.PUBLISHED,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")));
            } else {
                if (rangeStart == null) {
                    startDate = LocalDateTime.now();
                } else {
                    startDate = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME));
                }
                if (text == null) {
                    text = "";
                }
                if (rangeEnd == null) {
                    events = eventRepository.findEventsByText("%" + text.toLowerCase() + "%", EventState.PUBLISHED,
                            PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")));
                } else {
                    endDate = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME));
                    if (startDate.isAfter(endDate)) {
                        throw new ValidationException("Дата и время начала поиска не должна быть позже даты и времени конца поиска");
                    } else {
                        events = eventRepository.findAllByTextAndDateRange("%" + text.toLowerCase() + "%",
                                startDate,
                                endDate,
                                EventState.PUBLISHED,
                                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")));
                    }
                }
            }
        } else {
            if (rangeStart == null) {
                startDate = LocalDateTime.now();
            } else {
                startDate = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME));
            }
            if (rangeEnd == null) {
                endDate = null;
            } else {
                endDate = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME));
            }
            if (rangeStart != null && rangeEnd != null) {
                if (startDate.isAfter(endDate)) {
                    throw new ValidationException("Дата и время начала поиска не должна быть позже даты и времени конца поиска");
                }
            }
            events = eventRepository.findEventList(text, categories, paid, startDate, endDate, EventState.PUBLISHED);
        }
        if (!sortDate) {
            List<EventShortDto> shortEventDtos = createShortEventDtos(events);
            shortEventDtos.sort(Comparator.comparing(EventShortDto::getRating));
            shortEventDtos = shortEventDtos.subList(from, Math.min(from + size, shortEventDtos.size()));
            return shortEventDtos;
        }
        return createShortEventDtos(events);
    }

    @Override
    public List<EventFullDto> getRecommendations(Long userId) {

        log.info("Вывоз метода клиента: analyzerClient.getRecommendationsForUser with params userId = {}, maxResult", userId);
        List<Long> recommendationsForUser = analyzerClient.getRecommendationsForUser(
                UserPredictionsRequestProto.newBuilder()
                        .setUserId(userId)
                        .setMaxResult(10)
                        .build()
        ).stream().map(RecommendedEventProto::getEventId).collect(Collectors.toList());
        log.info("вывоз метода клиента analyzerClient.getRecommendationsForUser вернул данные: {}", StringUtils.join(recommendationsForUser, ','));

        List<Event> events = eventRepository.findAllById(recommendationsForUser);

        List<Long> usersIds = events.stream().map(Event::getInitiator).toList();
        Set<Long> categoriesIds = events.stream().map(Event::getCategory).collect(Collectors.toSet());
        Map<Long, UserDto> users = userClient.getUsersList(usersIds, 0, Math.max(events.size(), 1)).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        Map<Long, CategoryDto> categories = categoryClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        return events.stream().map(e -> EventMapper.mapEventToFullDto(
                e,
                requestClient.countByEventAndStatuses(e.getId(), List.of("CONFIRMED")),
                categories.get(e.getCategory()),
                users.get(e.getInitiator())
        )).collect(Collectors.toList());
    }

    @Override
    public void likeEvent(Long eventId, Long userId) throws ValidationException {
        if (!requestClient.isUserTakePart(userId, eventId)) {
            throw new ValidationException("Пользователь " + userId + " не принимал участи в событии " + eventId);
        }
        collectorClient.sendUserAction(createUserAction(eventId, userId, ActionTypeProto.ACTION_LIKE, Instant.now()));
    }

    List<EventShortDto> createShortEventDtos(List<Event> events) {
        List<EventRequestDto> requests = requestClient.findByEventIds(new ArrayList<>(
                events.stream().map(Event::getId).collect(Collectors.toList())
        ));
        List<Long> usersIds = events.stream().map(Event::getInitiator).toList();
        Set<Long> categoriesIds = events.stream().map(Event::getCategory).collect(Collectors.toSet());
        Map<Long, UserDto> users = userClient.getUsersList(usersIds, 0, Math.max(events.size(), 1)).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        Map<Long, CategoryDto> categories = categoryClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));
        return events.stream()
                .map(e -> EventMapper.mapEventToShortDto(e, categories.get(e.getCategory()), users.get(e.getInitiator())))
                .peek(dto -> dto.setConfirmedRequests(
                        requests.stream()
                                .filter((request -> request.getEvent().equals(dto.getId())))
                                .count()
                ))
                .peek(dto -> {
                    List<RecommendedEventProto> proto = analyzerClient.getInteractionsCount(getInteractionsRequest(dto.getId()));
                    dto.setRating(proto.isEmpty() ? 0.0 : proto.getFirst().getScore());

                })
                .collect(Collectors.toList());
    }

    private static InteractionsCountRequestProto getInteractionsRequest(Long eventId) {
        return InteractionsCountRequestProto.newBuilder().addEventId(eventId).build();
    }

    UserActionProto createUserAction(Long eventId, Long userId, ActionTypeProto type, Instant timestamp) {
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
