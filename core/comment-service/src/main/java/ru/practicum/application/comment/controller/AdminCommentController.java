package ru.practicum.application.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.application.api.dto.comment.CommentDto;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.request.comment.GetCommentsAdminRequest;
import ru.practicum.application.comment.service.CommentService;
import ru.practicum.application.comment.api.AdminCommentInterface;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController implements AdminCommentInterface {

    private final CommentService service;

    @Override
    public Collection<CommentDto> getComments(Long eventId, Integer from, Integer size) throws NotFoundException {
        return service.getAllEventComments(new GetCommentsAdminRequest(eventId, from, size));
    }

    @Override
    public void removeComment(@PathVariable("commentId") Long commentId) throws NotFoundException {
        log.info("удаление комментария с id {}", commentId);
        service.delete(commentId);
    }

}