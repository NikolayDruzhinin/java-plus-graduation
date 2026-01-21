package ru.practicum.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.event.enums.State;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepositoryCustom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<Event> getEvents(String text,
                                 List<Long> categories,
                                 Boolean paid,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 Boolean onlyAvailable,
                                 Boolean isAdmin,
                                 Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> eventRoot = cq.from(Event.class);
        List<Predicate> selectPreds = buildPredicates(
                cb, categories,
                eventRoot, text, paid,
                rangeStart, rangeEnd,
                onlyAvailable, isAdmin
        );
        cq.where(cb.and(selectPreds.toArray(new Predicate[0])));

        TypedQuery<Event> select = em.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        List<Event> content = select.getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Event> countRoot = countCq.from(Event.class);
        List<Predicate> countPreds = buildPredicates(
                cb, categories,
                countRoot, text, paid,
                rangeStart, rangeEnd,
                onlyAvailable, isAdmin
        );
        countCq.select(cb.count(countRoot))
                .where(cb.and(countPreds.toArray(new Predicate[0])));

        Long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            List<Long> categories,
                                            Root<Event> root,
                                            String text,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            Boolean isAdmin) {

        List<Predicate> p = new ArrayList<>();

        if (text != null && !text.isBlank()) {
            p.add(
                    cb.like(
                            cb.lower(root.get("annotation")),
                            "%" + text.toLowerCase() + "%"
                    )
            );
        }

        if (paid != null) {
            p.add(cb.equal(root.get("paid"), paid));
        }

        if (rangeStart != null) {
            p.add(cb.greaterThan(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            p.add(cb.lessThan(root.get("eventDate"), rangeEnd));
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            p.add(cb.greaterThan(root.get("participantLimit"), 0));
        }

        if (!Boolean.TRUE.equals(isAdmin)) {
            p.add(cb.equal(root.get("state"), State.PUBLISHED));
        }

        if (categories != null) {
            p.add(root.get("category").in(categories));
        }

        return p;
    }
}

