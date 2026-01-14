package ru.practicum.application.comment.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.application.api.dto.comment.CommentDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;

import java.util.Collection;

@Validated
public interface UserCommentInterface {
    @PostMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto addComments(
            @PathVariable Long userId,
            @RequestParam @Positive Long eventId,
            @RequestBody @Validated CommentDto commentDto
    ) throws ConflictException, NotFoundException;

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(
            @PathVariable @NonNull Long commentId,
            @PathVariable @NonNull Long userId
    ) throws ConflictException, NotFoundException;

    @PatchMapping("/users/{userId}/comments/{commentId}")
    CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentDto commentDto
    ) throws ConflictException, NotFoundException;

    @GetMapping("/users/{userId}/comments")
    Collection<CommentDto> getByUserComment(@PathVariable Long userId) throws NotFoundException;
}
