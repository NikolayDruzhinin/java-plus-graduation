package ru.practicum.comment.service;



import ru.practicum.dto.comment.CommentShortDto;

import java.util.List;

public interface AdminCommentService {

    List<CommentShortDto> getCommentsByParams(List<Long> userIds, List<Long> eventIds, Integer from, Integer size);

    void deleteCommentById(Long id);

}
