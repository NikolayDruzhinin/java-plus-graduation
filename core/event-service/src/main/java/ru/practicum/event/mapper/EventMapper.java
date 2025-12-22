package ru.practicum.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.client.UserClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final UserClient userServiceClient;

    public static Event toEvent(NewEventDto dto, User initiator, Category category) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setPaid(Boolean.TRUE.equals(dto.getPaid()));
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
        event.setRequestModeration(Boolean.TRUE.equals(dto.getRequestModeration()));
        event.setTitle(dto.getTitle());
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiatorId(initiator.getId());
        event.setCategory(category);
        event.setViews(0L);
        event.setConfirmedRequests(0L);

        return event;
    }

    public static void updateEventFromUserRequest(Event event, UpdateEventUserRequest dto, Category category) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getCategory() != null) {
            event.setCategory(category);
        }

    }

    public EventFullDto toEventFullDto(Event event) {
        User user = getUser(event.getInitiatorId());

        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setRequestModeration(event.isRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setInitiator(UserMapper.toUserShortDto(user));
        dto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0L);
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);
        return dto;
    }

    public EventFullDto toEventFullDto(Event event, User user) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setRequestModeration(event.isRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setInitiator(UserMapper.toUserShortDto(user));
        dto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0L);
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);
        return dto;
    }

    public List<EventFullDto> toEventFullDto(List<Event> events) {
        List<Long> userIds = events.stream().map(Event::getInitiatorId).toList();
        Map<Long, User> users = loadUsers(userIds);
        events.stream().collect(Collectors.toMap(Event::getId, Event::getCategory));
        return events.stream()
                .map(e -> toEventFullDto(e, users.get(e.getInitiatorId())))
                .toList();
    }

    public EventShortDto toEventShortDto(Event event) {
        User user = getUser(event.getInitiatorId());

        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.isPaid());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0L);
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);
        dto.setInitiator(UserMapper.toUserShortDto(user));
        dto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        return dto;
    }

    public EventShortDto toEventShortDto(Event event, User user) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.isPaid());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0L);
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);
        dto.setInitiator(UserMapper.toUserShortDto(user));
        dto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        return dto;
    }

    private Map<Long, User> loadUsers(List<Long> ids) {
        return userServiceClient.getUsersWithIds(ids).stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    private User getUser(Long userId) {
        return userServiceClient.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
