package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.Location;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

@Component
@RequiredArgsConstructor
public class EventDtoMapper {
    public Event mapToModel(NewEventDto dto, long userId) {
        return Event.builder()
                .initiatorId(userId)
                .annotation(dto.getAnnotation())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .locationLat(dto.getLocation().getLat())
                .locationLon(dto.getLocation().getLon())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public EventShortDto mapToShortDto(Event event, UserDto initiator, CategoryDto categoryDto) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests((int) event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id((int) event.getId())
                .initiator(mapUserDtoToShortDto(initiator))
                .paid(event.getPaid())
                .title(event.getTitle())
                .rating(event.getRating())
                .build();
    }

    public EventFullDto mapToFullDto(Event event, UserDto initiator, CategoryDto categoryDto) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests((int) event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(mapUserDtoToShortDto(initiator))
                .location(Location.builder()
                        .lat(event.getLocationLat())
                        .lon(event.getLocationLon())
                        .build())
                .paid(event.getPaid())
                .participantLimit((int) event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .rating(event.getRating())
                .build();
    }

    private UserShortDto mapUserDtoToShortDto(UserDto user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
