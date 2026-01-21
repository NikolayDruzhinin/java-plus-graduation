package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.model.Comment;

import java.time.LocalDateTime;

public interface CommentRepositoryCustom {

    Page<Comment> getComments(String content, Long userId, Long eventId,
                              LocalDateTime rangeStart, LocalDateTime  rangeEnd, Integer from, Integer size,
                              Pageable pageable);
}
