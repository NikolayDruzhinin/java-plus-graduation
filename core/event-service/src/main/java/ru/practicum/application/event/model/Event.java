package ru.practicum.application.event.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.application.api.dto.enums.EventState;
import ru.practicum.application.compilation.model.Compilation;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 1024)
    String annotation;

    @Column(name = "category")
    Long category;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column(length = 1024)
    String description;

    @Column(nullable = false, name = "event_date")
    LocalDateTime eventDate;

    @Column
    Long initiator;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    Location location;

    @Column
    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    EventState state;

    @Column(nullable = false)
    String title;

    @ManyToMany
    @JoinTable(name = "events_compilations",
            joinColumns = @JoinColumn(name = "event"),
            inverseJoinColumns = @JoinColumn(name = "compilation"))
    List<Compilation> compilationList;
}
