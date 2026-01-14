package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.UserClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSort;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsOutputDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private static final String APP_NAME = "ewm-main";

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;
    private final UserClient userClient;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event с id " + id + " не найден"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event с id " + id + " еще не опубликован");
        }

        addHit(request);
        updateEventViews(event);

        User u = getUserOrThrow(event.getInitiatorId());

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event, u);
        log.info("Получен eventFullDto с ID = {}", eventFullDto.getId());
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         EventSort sort,
                                         int from,
                                         int size,
                                         HttpServletRequest request) {

        validateTimeRange(rangeStart, rangeEnd);
        PageRequest page = PageRequest.of(from, size);
        Page<Event> pageEvents = getEventsFromRepository(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page);
        List<Event> events = pageEvents.getContent();

        addHit(request);
        List<EventShortDto> eventShortDtos = processEventsToShortDtos(events);

        if (sort != null) {
            sortEventDtos(eventShortDtos, sort);
        }

        return eventShortDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> getEventFullById(long id) {
        return eventRepository.findById(id);
    }

    private void validateTimeRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(1);

        if (end.isBefore(start)) {
            throw new BadRequestException("Недопустимый временной промежуток");
        }
    }

    private Page<Event> getEventsFromRepository(String text,
                                                List<Long> categories,
                                                Boolean paid,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                Boolean onlyAvailable,
                                                PageRequest page) {
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(1);

        return onlyAvailable
                ? eventRepository.findAllByPublicFiltersAndOnlyAvailable(text, categories, paid, start, end, page)
                : eventRepository.findAllByPublicFilters(text, categories, paid, start, end, page);
    }

    private List<EventShortDto> processEventsToShortDtos(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> viewsByEventId = getViewsForEvents(events);

        List<EventShortDto> dtos = events.stream()
                .map(event -> {
                    Long views = viewsByEventId.getOrDefault(event.getId(), event.getViews() != null ? event.getViews() : 0L);
                    event.setViews(views);
                    User u = getUserOrThrow(event.getInitiatorId());
                    return eventMapper.toEventShortDto(event, u);
                })
                .collect(Collectors.toList());

        updateEventsViewsInBatch(events, viewsByEventId);

        return dtos;
    }

    private User getUserOrThrow(Long userId) {
        return userClient.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Map<Long, Long> getViewsForEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        try {
            List<ViewStatsOutputDto> response = statsClient.getStats(
                    LocalDateTime.now().minusYears(999),
                    LocalDateTime.now().plusYears(1),
                    uris,
                    true
            );

            return response.stream()
                    .collect(Collectors.toMap(
                            stat -> Long.parseLong(stat.getUri().substring("/events/".length())),
                            ViewStatsOutputDto::getHits
                    ));
        } catch (Exception e) {
            log.error("Ошибка при получении статистики просмотров", e);
        }

        return Collections.emptyMap();
    }

    @Transactional
    protected void updateEventsViewsInBatch(List<Event> events, Map<Long, Long> viewsByEventId) {
        List<Event> eventsToUpdate = events.stream()
                .filter(event -> viewsByEventId.containsKey(event.getId()))
                .peek(event -> event.setViews(viewsByEventId.get(event.getId())))
                .collect(Collectors.toList());

        if (!eventsToUpdate.isEmpty()) {
            eventRepository.saveAll(eventsToUpdate);
        }
    }

    private void updateEventViews(Event event) {
        try {
            String uri = "/events/" + event.getId();
            List<ViewStatsOutputDto> response = statsClient.getStats(
                    LocalDateTime.now().minusYears(999),
                    LocalDateTime.now().plusYears(1),
                    List.of(uri),
                    true
            );

            if (!response.isEmpty()) {
                long hits = response.get(0).getHits();
                event.setViews(hits);
                eventRepository.save(event);
            }
        } catch (Exception e) {
            log.error("Ошибка при обновлении просмотров для события {}", event.getId(), e);
        }
    }

    private void sortEventDtos(List<EventShortDto> dtos, EventSort sort) {
        if (sort == EventSort.EVENT_DATE) {
            dtos.sort(Comparator.comparing(EventShortDto::getEventDate));
        } else {
            dtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
        }
    }

    private void addHit(HttpServletRequest request) {
        EndpointHitInputDto hit = new EndpointHitInputDto();
        hit.setApp(APP_NAME);
        hit.setUri(request.getRequestURI());
        hit.setIp(request.getRemoteAddr());
        hit.setTimestamp(LocalDateTime.now());

        try {
            statsClient.addHit(hit);
        } catch (Exception e) {
            log.error("Ошибка при отправке статистики просмотра", e);
        }
    }
}
