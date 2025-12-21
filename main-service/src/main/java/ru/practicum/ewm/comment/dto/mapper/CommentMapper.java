package ru.practicum.ewm.comment.dto.mapper;

import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.NewComment;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

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
