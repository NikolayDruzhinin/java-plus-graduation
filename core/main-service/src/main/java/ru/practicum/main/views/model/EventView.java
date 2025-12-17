package ru.practicum.main.views.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.main.event.entity.Event;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_views",
        uniqueConstraints = @UniqueConstraint(name = "uk_event_views_event_date",
                columnNames = {"event_id", "date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "ip", nullable = false)
    @Size(min = 7, max = 15)
    private String ip;
}
