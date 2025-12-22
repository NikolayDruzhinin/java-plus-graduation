package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        SELECT c
        FROM Comment c
        WHERE (:userIds is null or c.author.id in :userIds)
        AND (:eventIds is null or c.event.id in :eventIds)
        """)
    List<Comment> findByUserIdInAndEventIdIn(@Param("userIds") List<Long> userIds, @Param("eventIds") List<Long> eventIds, Pageable pageable);

    List<Comment> findAllByEventId(long id);

    List<Comment> findByAuthorId(Long userId, Pageable pageable);
}
