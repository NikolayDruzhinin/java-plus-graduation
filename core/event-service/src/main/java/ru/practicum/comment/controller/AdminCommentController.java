package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.service.AdminCommentService;
import ru.practicum.dto.comment.CommentShortDto;


import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Validated
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping
    public List<CommentShortDto> getCommentsByParams(@RequestParam(required = false) List<Long> userIds,
                                                     @RequestParam(required = false) List<Long> eventIds,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр 'from' не может быть отрицательным") Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive(message = "Параметр 'size' должен быть больше 0") Integer size) {
        log.info("Поступил запрос Get /admin/comments на получение List<CommentShortDto> с параметрами userIds = {}, eventIds = {}, from = {}, size = {}", userIds, eventIds, from, size);
        List<CommentShortDto> response = adminCommentService.getCommentsByParams(userIds, eventIds, from, size);
        log.info("Сформирован ответ Get /admin/comments с телом: {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        log.info("Поступил запрос Delete /admin/comments/{} на удаление Comment с id = {}", id, id);
        adminCommentService.deleteCommentById(id);
        log.info("Выполнен запрос Delete /admin/comments/{} на удаление Comment с id = {}", id, id);
    }
}
