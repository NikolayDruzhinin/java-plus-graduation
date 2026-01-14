package ru.practicum.application.event.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.application.api.dto.event.EventFullDto;
import ru.practicum.application.api.dto.event.EventShortDto;
import ru.practicum.application.api.exception.NotFoundException;

import java.util.List;

public interface InnerEventInterface {
    @GetMapping("/inner/event/{eventId}")
    EventFullDto getInnerEventById(@PathVariable Long eventId) throws NotFoundException;

    @GetMapping("/inner/event/{eventId}/exist")
    boolean existsById(@PathVariable Long eventId);

    @GetMapping("/inner/event/category/{categoryId}/exist")
    boolean existsByCategoryId(@PathVariable Long categoryId);

    @GetMapping("/inner/event/short/ids")
    List<EventShortDto> getShortByIds(@RequestParam List<Long> ids);
}
