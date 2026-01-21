package ru.practicum.client.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.MyFeignClientFallback;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;

import java.util.List;

@FeignClient(name = "event-service", fallback = MyFeignClientFallback.class)
public interface EventClient {

    @PostMapping("/users/{userId}/events")
    EventFullDto saveEvent(@RequestBody NewEventDto newEventDto,
                           @PathVariable Long userId);

    @PatchMapping("/users/{userId}/events/{eventId}")
    EventFullDto updateEventByIdAndUserId(@RequestBody UpdatedEventDto updatedEventDto,
                                          @PathVariable Long userId,
                                          @PathVariable Long eventId);

    @PatchMapping("/admin/events/{eventId}")
    EventFullDto updateAdminEventByIdAndUserId(@RequestBody UpdatedEventDto updatedEventDto,
                                               @PathVariable Long eventId);

    @GetMapping("/events/{eventId}")
    EventFullDto getEventById(@PathVariable Long eventId, @RequestHeader("X-EWM-USER-ID") Long userId);

    @GetMapping("/events/recommendations")
    List<EventShortDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId);

    @GetMapping("/events")
    List<EventFullDto> getEvents(@RequestParam(required = false) String text,
                                 @RequestParam(required = false) List<Long> categories,
                                 @RequestParam(required = false) Boolean paid,
                                 @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                 @RequestParam(required = false) String rangeStart,
                                 @RequestParam(required = false) String rangeEnd,
                                 @RequestParam(required = false) String sort,
                                 @RequestParam(defaultValue = "0") Integer from,
                                 @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/users/{userId}/events")
    List<EventShortDto> getEventsByUserId(@PathVariable Long userId,
                                          @RequestParam(required = false) Integer from,
                                          @RequestParam(required = false) Integer size);

    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getEventByUserIdAndEventId(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId);

    @GetMapping("/admin/events")
    List<EventFullDto> getAdminEvents(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(required = false) String rangeStart,
                                      @RequestParam(required = false) String rangeEnd,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size);

    @PostMapping("/admin/events/increment")
    EventFullDto saveFullEvent(@RequestBody EventFullDto event);
}
