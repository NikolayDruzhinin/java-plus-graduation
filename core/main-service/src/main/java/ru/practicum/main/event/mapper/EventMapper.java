package ru.practicum.main.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.category.entity.Category;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.entity.Event;
import ru.practicum.main.event.entity.EventState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toEntity(NewEventDto dto, Category category, Long initiatorId) {
        return Event.builder()
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(LocalDateTime.parse(dto.getEventDate(), F))
                .createdOn(LocalDateTime.now())
                .lat(dto.getLocation() != null ? dto.getLocation().getLat() : null)
                .lon(dto.getLocation() != null ? dto.getLocation().getLon() : null)
                .paid(Boolean.TRUE.equals(dto.getPaid()))
                .participantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() == null || dto.getRequestModeration())
                .state(EventState.PENDING)
                .views(0L)
                .category(category)
                .initiatorId(initiatorId)
                .build();
    }

    public static EventFullDto toFullDto(Event e) {
        return EventFullDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .createdOn(e.getCreatedOn())
                .publishedOn(e.getPublishedOn())
                .location(new LocationDto(e.getLat(), e.getLon()))
                .paid(e.getPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.getRequestModeration())
                .state(e.getState())
                .views(e.getViews())
                .build();
    }

    public static EventShortDto toShortDto(Event e) {
        return EventShortDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .annotation(e.getAnnotation())
                .eventDate(e.getEventDate())
                .paid(e.getPaid())
                .views(e.getViews())
                .build();
    }
}

