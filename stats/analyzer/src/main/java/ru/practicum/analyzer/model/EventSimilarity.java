package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "event_similarities", schema = "events")
@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Double score;

    @NotNull
    Long eventA;

    @NotNull
    Long eventB;

    @NotNull
    Instant timestamp;
}

