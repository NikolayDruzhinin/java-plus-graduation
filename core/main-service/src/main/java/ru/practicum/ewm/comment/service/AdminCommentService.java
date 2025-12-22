package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentShortDto;

import java.util.List;

public interface AdminCommentService {

    List<CommentShortDto> getCommentsByParams(List<Long> userIds, List<Long> eventIds, Integer from, Integer size);

    void deleteCommentById(Long id);

}
