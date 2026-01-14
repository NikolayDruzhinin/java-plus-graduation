package ru.practicum.application.comment.api;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.application.api.dto.comment.CommentDto;
import ru.practicum.application.api.exception.NotFoundException;

import java.util.Collection;

public interface CommentInterface {
    @GetMapping("/events/{eventId}/comments")
    Collection<CommentDto> getByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) throws NotFoundException;
}
