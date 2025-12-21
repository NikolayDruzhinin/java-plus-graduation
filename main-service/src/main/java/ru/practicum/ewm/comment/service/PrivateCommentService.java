package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.NewComment;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;

import java.util.List;

public interface PrivateCommentService {

    CommentShortDto createComment(Long userId, Long eventId, NewComment newComment);

    List<CommentShortDto> getUserComments(Long userId, Integer from, Integer size);

    CommentShortDto updateComment(Long userId, Long commentId, UpdateCommentDto updateComment);

    void deleteComment(Long userId, Long commentId);
}
