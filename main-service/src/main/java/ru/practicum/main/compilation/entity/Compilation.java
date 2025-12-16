package ru.practicum.main.compilation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.main.event.entity.Event;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private boolean pinned;
    private LocalDateTime created;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilation_events",
            joinColumns = {@JoinColumn(name = "compilation_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id")})
    private Set<Event> events = new HashSet<>();
}
