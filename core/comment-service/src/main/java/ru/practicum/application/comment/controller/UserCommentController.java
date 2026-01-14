package ru.practicum.application.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.application.api.dto.comment.CommentDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.comment.api.UserCommentInterface;
import ru.practicum.application.comment.service.CommentService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
public class UserCommentController implements UserCommentInterface {

    private final CommentService service;

    @Override
    public CommentDto addComments(Long userId, Long eventId, CommentDto commentDto) throws ConflictException, NotFoundException {
        return service.addComment(commentDto, userId, eventId);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) throws ConflictException, NotFoundException {
        service.delete(userId, commentId);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto) throws ConflictException, NotFoundException {
        return service.updateUserComment(userId, commentId, commentDto);
    }

    @Override
    public Collection<CommentDto> getByUserComment(Long userId) throws NotFoundException {
        return service.getAllUserComments(userId);
    }
}