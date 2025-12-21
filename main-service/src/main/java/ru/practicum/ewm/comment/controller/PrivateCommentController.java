package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.NewComment;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.PrivateCommentService;
import ru.practicum.ewm.validation.CreateGroup;
import ru.practicum.ewm.validation.UpdateGroup;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/")
@Validated
public class PrivateCommentController {

    private final PrivateCommentService privateCommentService;

    @PostMapping("{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody @Validated(CreateGroup.class) NewComment newComment) {
        log.info("Поступил запрос Post /users/{}/events/{}/comments на создание комментария с телом: {}", userId, eventId, newComment);
        CommentShortDto response = privateCommentService.createComment(userId, eventId, newComment);
        log.info("Сформирован ответ Post /users/{}/events/{}/comments с телом: {}", userId, eventId, response);
        return response;
    }

    @GetMapping("{userId}/comments")
    public List<CommentShortDto> getUserComments(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос GET /users/{}/comments на получение списка комментариев с параметрами from={}, size={}", userId, from, size);
        List<CommentShortDto> response = privateCommentService.getUserComments(userId, from, size);
        log.info("Сформирован ответ GET /users/{}/comments с {} комментариями", userId, response.size());
        return response;
    }

    @PatchMapping("{userId}/comments/{commentId}")
    public CommentShortDto updateComment(@PathVariable Long userId,
                                         @PathVariable Long commentId,
                                         @RequestBody @Validated(UpdateGroup.class) UpdateCommentDto updateComment) {
        log.info("Поступил запрос PATCH /users/{}/comments/{} на обновление комментария с телом: {}", userId, commentId, updateComment);
        CommentShortDto response = privateCommentService.updateComment(userId, commentId, updateComment);
        log.info("Сформирован ответ PATCH /users/{}/comments/{} с телом: {}", userId, commentId, response);
        return response;
    }

    @DeleteMapping("{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Поступил запрос DELETE /users/{}/comments/{} на удаление комментария", userId, commentId);
        privateCommentService.deleteComment(userId, commentId);
        log.info("Выполнено удаление комментария DELETE /users/{}/comments/{}", userId, commentId);
    }
}
