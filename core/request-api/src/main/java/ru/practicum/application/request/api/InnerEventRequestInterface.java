package ru.practicum.application.request.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.application.api.dto.request.EventRequestDto;

import java.util.List;

public interface InnerEventRequestInterface {
    @GetMapping("/inner/request/{eventId}/status/count")
    Long countByEventAndStatuses(@PathVariable Long eventId, @RequestParam List<String> statuses);
    @GetMapping("/inner/request/events/{status}")
    List<EventRequestDto> getByEventAndStatus(@RequestParam List<Long> eventId, @PathVariable String status);

    @GetMapping("/inner/request/events")
    List<EventRequestDto> findByEventIds(@RequestParam List<Long> id);

    @GetMapping("/inner/{userId}/take/{eventId}")
    boolean isUserTakePart(@PathVariable Long userId, @PathVariable Long eventId);
}
