package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.NewComment;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentShortDto createComment(Long userId, Long eventId, NewComment newComment) {
        User user = checkUserExist(userId);
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

    private User checkUserExist(Long id) {
        return userRepository.findById(id)
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
