package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.client.event.EventClient;
import ru.practicum.client.user.UserAdminClient;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.DeleteCommentsDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.enums.State;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictRelationsConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventClient eventClient;
    private final UserAdminClient userAdminClient;


    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        EventFullDto event = getEvent(eventId, userId);
        if(!event.getState().equals(State.PUBLISHED))
            throw new ConflictRelationsConstraintException("Нельзя добавить комментарий если событие не опубликовано");

        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .content(newCommentDto.getContent())
                .eventId(eventId)
                .userId(userId)
                .build();

        return CommentMapper.commentToDto(commentRepository.save(comment));
    }


    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = getComment(commentId);
        getCommentByUserId(userId, commentId);

        comment.setContent(updateCommentDto.getContent());

        return CommentMapper.commentToDto(commentRepository.save(comment));

    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        getUser(userId);
        getCommentByUserId(userId, commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getComments(String content, Long userId, Long eventId,
                                        String rangeStart, String rangeEnd, Integer from, Integer size) {
        UserDto user = null;
        EventFullDto event = null;

        if (userId != null) {
            user = getUser(userId);
            if (eventId != null) {
                event = getEvent(eventId, userId);
            };
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = LocalDateTime.of(1900, 1, 1, 1, 1).format(formatter);
        }

        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = null;

        if (rangeEnd == null || rangeEnd.isBlank()) {
            rangeEnd = LocalDateTime.now().format(formatter);
        }
        end = LocalDateTime.parse(rangeEnd, formatter);
        if (start.isAfter(end)) {
            throw new BadRequestException("В диапазоне времени поиска начало не может быть позже конца");
        }

        int safeFrom = (from != null) ? from : 0;
        int safeSize = (size == null ? 100 : size);
        PageRequest page = PageRequest.of(safeFrom / safeSize, safeSize);

        return commentRepository.getComments(content, userId, eventId,
                        start, end, from, size, page)
                .stream()
                .map(CommentMapper::commentToDto)
                .toList();
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        return CommentMapper.commentToDto(getComment(commentId));
    }

    @Override
    public List<CommentDto> getCommentsByUserId(Long userId) {
        getUser(userId);
        List<Comment> commentsList = commentRepository.findByUserId(userId);
        return commentsList.stream()
                .map(CommentMapper::commentToDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteCommentsByAdmin(DeleteCommentsDto deleteCommentsDto) {
        List<Long> commentListIds = commentRepository.findByIdIn(deleteCommentsDto.getCommentsIds()).stream()
                .map(Comment::getId)
                .toList();

        List<Long> commentIdsNotExist = deleteCommentsDto.getCommentsIds().stream()
                .filter(commentId -> !commentListIds.contains(commentId))
                .toList();

        if(!commentIdsNotExist.isEmpty())
            throw new NotFoundException("Комментарии с id: " + commentIdsNotExist + " не найдены");

        commentRepository.deleteByIdIn(deleteCommentsDto.getCommentsIds());

    }

    private UserDto getUser(Long userId) {
        return userAdminClient.getUserById(userId);
    }

    private EventFullDto getEvent(Long eventId, Long userId) {
        return eventClient.getEventById(eventId, userId);
    }

    private Comment getCommentByUserId(Long userId, Long commentId) {
        return commentRepository.findByUserIdAndId(userId, commentId).orElseThrow(
                () -> new ConflictRelationsConstraintException(
                        "Пользователю id: " + userId + " не принадлежит комментарий с id: " + commentId)
        );
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментария с id: " + commentId + " не существует")
        );
    }
}
