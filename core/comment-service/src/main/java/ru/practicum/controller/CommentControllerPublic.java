package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.CommentService;
import ru.practicum.dto.comment.CommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentControllerPublic {

    private final CommentService commentService;

    @GetMapping("/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@RequestParam(required = false) String content,
                                        @RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) Long eventId,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(required = false) Integer from,
                                        @RequestParam(required = false) Integer size) {
        return commentService.getComments(content, userId, eventId, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentByID(@PathVariable Long commentId) {

        return commentService.getCommentById(commentId);
    }
}
