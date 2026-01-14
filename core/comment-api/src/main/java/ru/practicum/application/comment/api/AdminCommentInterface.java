package ru.practicum.application.comment.api;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.application.api.dto.comment.CommentDto;
import ru.practicum.application.api.exception.NotFoundException;

import java.util.Collection;

@Validated
public interface AdminCommentInterface {
    @GetMapping("/admin/comments")
    Collection<CommentDto> getComments(
            @RequestParam("eventId") @Positive Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) throws NotFoundException;

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeComment(@PathVariable("commentId") Long commentId) throws NotFoundException;
}
