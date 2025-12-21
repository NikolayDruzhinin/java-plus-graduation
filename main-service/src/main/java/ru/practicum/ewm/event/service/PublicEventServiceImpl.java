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

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private static final String APP_NAME = "ewm-main";

    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event c id " + id + "не найден"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event c id " + id + "еще не опубликован");
        }

        addHit(request);
        updateEventViewsInRepository(event);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

        log.info("получен eventFullDto с ID = {}", eventFullDto.getId());
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

        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(1);

        if (end.isBefore(start)) {
            throw new BadRequestException("Недопустимый временной промежуток, время окончание поиска не может быть раньше времени начала поиска");
        }


        PageRequest page = PageRequest.of(from, size);
        Page<Event> pageEvents;
        if (onlyAvailable) {
            pageEvents = eventRepository.findAllByPublicFiltersAndOnlyAvailable(text, categories, paid, start, end, page);
        } else {
            pageEvents = eventRepository.findAllByPublicFilters(text, categories, paid, start, end, page);
        }

        List<Event> events = pageEvents.getContent();

        addHit(request);

        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            Event ev = updateEventViewsInRepository(event);
            EventShortDto dto = EventMapper.toEventShortDto(ev);
            eventShortDtos.add(dto);
        }

        if (sort != null) {
            if (sort.equals(EventSort.EVENT_DATE)) {
                eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
            } else {
                eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
            }
        } else {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
        }

        return eventShortDtos;
    }

    private void addHit(HttpServletRequest request) {
        EndpointHitInputDto hit = new EndpointHitInputDto();
        hit.setApp(APP_NAME);
        hit.setUri(request.getRequestURI());
        hit.setIp(request.getRemoteAddr());
        hit.setTimestamp(LocalDateTime.now());
        statsClient.addHit(hit);
    }

    private Event updateEventViewsInRepository(Event event) {

        try {
            Long eventId = event.getId();
            String eventUri = "/events/" + eventId;
            ResponseEntity<Object> responseEntity = statsClient.getStats(LocalDateTime.now().minusYears(999), LocalDateTime.now().plusYears(1), List.of(eventUri), true);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> responseBody = objectMapper.convertValue(responseEntity.getBody(), new TypeReference<List<Map<String, Object>>>() {
                });

                List<ViewStatsOutputDto> responseList = responseBody.stream()
                    .map(map -> new ViewStatsOutputDto((String) map.get("app"), (String) map.get("uri"), ((Number) map.get("hits")).longValue()))
                    .toList();

                if (!responseList.isEmpty()) {
                    ViewStatsOutputDto viewStatsOutputDto = responseList.getFirst();
                    event.setViews(viewStatsOutputDto.getHits());
                    return eventRepository.save(event);
                }
            }
            return event;
        } catch (Exception e) {
            return event;
        }
    }
}
