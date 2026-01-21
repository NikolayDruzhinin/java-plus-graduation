package ru.practicum.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepositoryCustom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<Comment> getComments(String content, Long userId, Long eventId,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size,
                                     Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Comment> cq = cb.createQuery(Comment.class);
        Root<Comment> commentRoot = cq.from(Comment.class);

        commentRoot.fetch("user", JoinType.LEFT); // должно подгружать сущности User и Event по id с @ManyToOne
        commentRoot.fetch("event", JoinType.LEFT); // для возврата (в Comment хранится userId и EventId)

        List<Predicate> selectPreds = buildPredicates(
                cb, commentRoot, content, userId, eventId,
                rangeStart, rangeEnd
        );
        cq.where(cb.and(selectPreds.toArray(new Predicate[0])));

        TypedQuery<Comment> select = em.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        List<Comment> contents = select.getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Comment> countRoot = countCq.from(Comment.class);
        List<Predicate> countPreds = buildPredicates(
                cb, countRoot, content, userId, eventId,
                rangeStart, rangeEnd
        );
        countCq.select(cb.count(countRoot))
                .where(cb.and(countPreds.toArray(new Predicate[0])));

        Long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(contents, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<Comment> root,
                                            String content, Long userId, Long eventId,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd) {

        List<Predicate> p = new ArrayList<>();

        if (content != null && !content.isBlank()) {
            p.add(
                    cb.like(
                            cb.lower(root.get("content")),
                            "%" + content.toLowerCase() + "%"
                    )
            );
        }

        if (userId != null) {
            p.add(cb.equal(root.get("user").get("id"), userId));
        }

        if (eventId != null) {
            p.add(cb.equal(root.get("event").get("id"), eventId));
        }

        if (rangeStart != null) {
            p.add(cb.greaterThan(root.get("created"), rangeStart));
        }
        if (rangeEnd != null) {
            p.add(cb.lessThan(root.get("created"), rangeEnd));
        }

        return p;
    }
}
