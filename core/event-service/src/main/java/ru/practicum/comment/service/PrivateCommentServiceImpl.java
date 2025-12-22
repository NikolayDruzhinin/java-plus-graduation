package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.UserClient;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewComment;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserClient userServiceClient;

    @Override
    @Transactional
    public CommentShortDto createComment(Long userId, Long eventId, NewComment newComment) {
        User user = getUser(userId);
        Event event = checkEventExist(eventId);
        Comment comment = CommentMapper.fromNewCommentToComment(newComment, user, event);
        return CommentMapper.toCommentShortDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentShortDto> getUserComments(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return CommentMapper.toCommentShortDto(commentRepository.findByAuthorId(userId, pageable));
    }

    @Override
    @Transactional
    public CommentShortDto updateComment(Long userId, Long commentId, UpdateCommentDto updateComment) {
        Comment comment = checkCommentExistAndAuthor(commentId, userId);
        comment.setText(updateComment.getText());
        comment.setIsUpdated(true);
        comment.setUpdatedOn(LocalDateTime.now());
        return CommentMapper.toCommentShortDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = checkCommentExistAndAuthor(commentId, userId);
        commentRepository.delete(comment);
    }

    private Event checkEventExist(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("События с id = " + id + " не существует"));
    }

    private User getUser(Long id) {
        return userServiceClient.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + id + " не существует"));
    }

    private Comment checkCommentExistAndAuthor(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий с id = " + commentId + " не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является автором комментария");
        }
        return comment;
    }
}
