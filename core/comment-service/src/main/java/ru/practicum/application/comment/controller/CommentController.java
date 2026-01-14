package ru.practicum.application.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.application.api.dto.comment.CommentDto;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.comment.api.CommentInterface;
import ru.practicum.application.comment.service.CommentService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
public class CommentController implements CommentInterface {

    private final CommentService service;

    @Override
    public Collection<CommentDto> getByEvent(Long eventId, Integer from, Integer size) throws NotFoundException {
        return service.getAllEventComments(eventId, from, size);
    }
}

