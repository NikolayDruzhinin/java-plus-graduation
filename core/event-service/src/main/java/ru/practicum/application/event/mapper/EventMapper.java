package ru.practicum.application.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.application.api.dto.category.CategoryDto;
import ru.practicum.application.api.dto.enums.EventState;
import ru.practicum.application.api.dto.event.EventFullDto;
import ru.practicum.application.api.dto.event.EventShortDto;
import ru.practicum.application.api.dto.event.LocationDto;
import ru.practicum.application.api.dto.event.NewEventDto;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.model.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.application.api.util.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

@UtilityClass
public class EventMapper {
    public EventFullDto mapEventToFullDto(Event event, Long confirmed, CategoryDto category, UserDto user) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(category);
        eventFullDto.setConfirmedRequests(confirmed);
        eventFullDto.setCreatedOn(getLocalDateTime(event.getCreatedOn()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(getLocalDateTime(event.getEventDate()));
        eventFullDto.setInitiator(user);
        eventFullDto.setLocation(mapLocationToDto(event.getLocation()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(getLocalDateTime(event.getPublishedOn()));
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState() == null ? EventState.PENDING : event.getState());
        eventFullDto.setTitle(event.getTitle());
        return eventFullDto;
    }

    public EventShortDto mapEventToShortDto(Event event, CategoryDto category, UserDto user) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setCategory(category);
        eventShortDto.setInitiator(user);
        eventShortDto.setId(event.getId());
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setEventDate(event.getEventDate());
        return eventShortDto;
    }

    public Event mapNewEventDtoToEvent(NewEventDto newEvent, CategoryDto category) {
        Event event = new Event();
        event.setAnnotation(newEvent.getAnnotation());
        event.setCategory(category.getId());
        event.setDescription(newEvent.getDescription());
        event.setEventDate(getFromString(newEvent.getEventDate()));
        event.setLocation(mapDtoToLocation(newEvent.getLocation()));
        event.setPaid(newEvent.getPaid());
        event.setParticipantLimit(newEvent.getParticipantLimit());
        event.setRequestModeration(newEvent.getRequestModeration());
        event.setTitle(newEvent.getTitle());
        return event;
    }

    static LocationDto mapLocationToDto(Location location) {
        return new LocationDto(location.getId(), location.getLat(), location.getLon());
    }

    public Location mapDtoToLocation(LocationDto locationDto) {
        return new Location(locationDto.getId(), locationDto.getLat(), locationDto.getLon());
    }

    static String getLocalDateTime(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME));
    }

    static LocalDateTime getFromString(String time) {
        if (time == null) {
            return null;
        }
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME));
    }
}
