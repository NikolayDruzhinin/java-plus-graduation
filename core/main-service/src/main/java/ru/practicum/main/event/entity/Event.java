package ru.practicum.main.event.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.category.entity.Category;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String annotation;

    @Column(length = 7000, nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "location_lat")
    private Float lat;

    @Column(name = "location_lon")
    private Float lon;

    @Column(nullable = false)
    private Boolean paid = Boolean.FALSE;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit = 0;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration = Boolean.TRUE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state = EventState.PENDING;

    @Column(nullable = false)
    private long views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "initiator_id", nullable = false)
    private Long initiatorId;

}

