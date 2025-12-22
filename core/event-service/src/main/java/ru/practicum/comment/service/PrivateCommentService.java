package ru.practicum.comment.service;



import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewComment;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

public interface PrivateCommentService {

    CommentShortDto createComment(Long userId, Long eventId, NewComment newComment);

    List<CommentShortDto> getUserComments(Long userId, Integer from, Integer size);

    CommentShortDto updateComment(Long userId, Long commentId, UpdateCommentDto updateComment);

    void deleteComment(Long userId, Long commentId);
}
