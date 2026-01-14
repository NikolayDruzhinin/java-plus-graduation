package ru.practicum.stats.analyzer.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "event_similarities")
@IdClass(EventSimilarityId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class EventSimilarity {
    @Id
    @Column(name = "first_event")
    Long first;
    @Id
    @Column(name = "second_event")
    Long second;
    Double score;
}
