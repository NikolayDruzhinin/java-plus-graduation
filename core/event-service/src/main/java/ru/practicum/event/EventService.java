package ru.practicum.event;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;

import java.util.List;

public interface EventService {

    EventFullDto saveEvent(NewEventDto newEventDto, long userId);

    List<EventFullDto> getEvents(String text, List<Long> categories, Boolean paid,
                                 String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                 String sort, Integer from, Integer size, String user);

    EventFullDto getEventById(Long eventId, Long userId);

    List<EventShortDto> getEventsByUserId(long userId, Integer from, Integer size);

    EventFullDto getEventByUserIdAndEventId(long userId, long eventId);

    EventFullDto updateEvent(UpdatedEventDto updatedEventDto,
                                       long userId, long eventId);

    EventFullDto updateAdminEvent(UpdatedEventDto updatedEventDto,
                                            long eventId);

    EventFullDto saveFullEvent(EventFullDto event);

    Integer checkInitiatorEvent(Long initiatorId, Long eventId);

    List<EventShortDto> getRecommendations(long userId);

    void likeEvent(Long eventId, Long userId);
}