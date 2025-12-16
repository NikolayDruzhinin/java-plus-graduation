package ru.practicum.main.event.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.main.event.entity.Event;
import ru.practicum.main.event.entity.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> adminSearch(
            List<Long> users, List<EventState> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd
    ) {
        return (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (users != null && !users.isEmpty()) {
                ps.add(root.get("initiatorId").in(users));
            }
            if (states != null && !states.isEmpty()) {
                ps.add(root.get("state").in(states));
            }
            if (categories != null && !categories.isEmpty()) {
                ps.add(root.get("category").get("id").in(categories));
            }
            if (rangeStart != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
    }

    public static Specification<Event> publicSearch(
            String text, List<Long> categories, Boolean paid,
            LocalDateTime rangeStart, LocalDateTime rangeEnd
    ) {
        return (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("state"), EventState.PUBLISHED));

            if (text != null && !text.isBlank()) {
                String pattern = "%" + text.toLowerCase() + "%";
                Predicate byAnn = cb.like(cb.lower(root.get("annotation")), pattern);
                Predicate byDesc = cb.like(cb.lower(root.get("description")), pattern);
                ps.add(cb.or(byAnn, byDesc));
            }
            if (categories != null && !categories.isEmpty()) {
                ps.add(root.get("category").get("id").in(categories));
            }
            if (paid != null) {
                ps.add(cb.equal(root.get("paid"), paid));
            }
            if (rangeStart != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
    }
}

