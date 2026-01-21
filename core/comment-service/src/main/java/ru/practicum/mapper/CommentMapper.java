package ru.practicum.mapper;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.model.Comment;

public class CommentMapper {
    public static CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(comment.getUserId())
                .event(comment.getEventId())
                .build();
    }
}