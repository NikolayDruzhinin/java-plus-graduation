package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentShortDto;

import java.util.List;

public interface PublicCommentService {
    List<CommentShortDto> getAllByEventId(long id);
}
