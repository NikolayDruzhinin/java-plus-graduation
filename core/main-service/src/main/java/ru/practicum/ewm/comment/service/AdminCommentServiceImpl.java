package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.comment.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;

    @Override
    public List<CommentShortDto> getCommentsByParams(List<Long> userIds, List<Long> eventIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return CommentMapper.toCommentShortDto(commentRepository.findByUserIdInAndEventIdIn(userIds, eventIds, pageable));
    }

    @Override
    @Transactional
    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }

}
