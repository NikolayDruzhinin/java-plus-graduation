package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Optional<Comment> findByUserIdAndId(Long userId, Long commentId);

    List<Comment> findByUserId(Long userId);

    List<Comment> findByIdIn(List<Long> commentIds);

    void deleteByIdIn(List<Long> commentIds);

}
