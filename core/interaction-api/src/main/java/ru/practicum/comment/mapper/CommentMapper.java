package ru.practicum.comment.mapper;

import ru.practicum.comment.model.Comment;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewComment;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static Comment fromNewCommentToComment(NewComment newComment, User author, Event event) {
        Comment comment = new Comment();
        comment.setText(newComment.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setPublishedOn(LocalDateTime.now());
        comment.setIsUpdated(false);
        return comment;
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        CommentShortDto commentShortDto = new CommentShortDto();
        commentShortDto.setId(comment.getId());
        commentShortDto.setEventId(comment.getEvent().getId());
        commentShortDto.setAuthor(UserMapper.toUserShortDto(comment.getAuthor()));
        commentShortDto.setText(comment.getText());
        commentShortDto.setPublishedOn(comment.getPublishedOn());
        commentShortDto.setIsUpdated(comment.getIsUpdated());
        commentShortDto.setUpdatedOn(comment.getUpdatedOn());
        return commentShortDto;
    }

    public static List<CommentShortDto> toCommentShortDto(Iterable<Comment> comments) {
        List<CommentShortDto> shortComments = new ArrayList<>();
        for (Comment comment : comments) {
            shortComments.add(toCommentShortDto(comment));
        }
        return shortComments;
    }
}
