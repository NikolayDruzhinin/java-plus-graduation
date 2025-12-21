package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsOutputDto;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.dto.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private static final String APP_NAME = "ewm-main";
    private static final String EVENTS_URI_PREFIX = "/events/";

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

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

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
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

        PageRequest page = PageRequest.of(from / size, size);
        Page<Event> pageEvents = getFilteredEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page);

        List<Event> events = pageEvents.getContent();
        addHit(request);
        updateEventsViews(events);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        sortEventDtos(eventShortDtos, sort);

        return eventShortDtos;
    }

    private void validateTimeRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : start.plusYears(1);

        if (end.isBefore(start)) {
            throw new BadRequestException("Недопустимый временной промежуток");
        }
    }

    private Page<Event> getFilteredEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Boolean onlyAvailable,
                                          PageRequest page) {
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : start.plusYears(1);

        return onlyAvailable
                ? eventRepository.findAllByPublicFiltersAndOnlyAvailable(text, categories, paid, start, end, page)
                : eventRepository.findAllByPublicFilters(text, categories, paid, start, end, page);
    }

    private void updateEventsViews(List<Event> events) {
        if (events.isEmpty()) {
            return;
        }

        List<String> eventUris = events.stream()
                .map(event -> EVENTS_URI_PREFIX + event.getId())
                .collect(Collectors.toList());

        Map<Long, Long> eventViews = getEventsViews(eventUris);

        events.forEach(event -> {
            Long views = eventViews.getOrDefault(event.getId(), 0L);
            event.setViews(views);
        });

        eventRepository.saveAll(events);
    }

    private Map<Long, Long> getEventsViews(List<String> uris) {
        try {
            ResponseEntity<Object> response = statsClient.getStats(
                    LocalDateTime.now().minusYears(1),
                    LocalDateTime.now().plusHours(1),
                    uris,
                    true);

            if (response.getStatusCode().is2xxSuccessful()) {
                List<ViewStatsOutputDto> stats = objectMapper.convertValue(
                        response.getBody(),
                        new TypeReference<>() {});

                return stats.stream()
                        .collect(Collectors.toMap(
                                stat -> Long.parseLong(stat.getUri().substring(EVENTS_URI_PREFIX.length())),
                                ViewStatsOutputDto::getHits
                        ));
            }
        } catch (Exception e) {
            log.error("Failed to get views stats", e);
        }
        return Collections.emptyMap();
    }

    private void updateEventViews(Event event) {
        String eventUri = EVENTS_URI_PREFIX + event.getId();
        Map<Long, Long> views = getEventsViews(List.of(eventUri));
        event.setViews(views.getOrDefault(event.getId(), 0L));
        eventRepository.save(event);
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
            log.error("Failed to send hit to stats service", e);
        }
    }

    private void sortEventDtos(List<EventShortDto> dtos, EventSort sort) {
        if (sort == EventSort.EVENT_DATE) {
            dtos.sort(Comparator.comparing(EventShortDto::getEventDate));
        } else {
            dtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }
    }
}
