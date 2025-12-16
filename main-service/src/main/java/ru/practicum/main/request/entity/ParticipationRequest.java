package ru.practicum.main.request.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "participation_requests",
        uniqueConstraints = @UniqueConstraint(name = "uq_request_event_requester", columnNames = {"event_id", "requester_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime created;
}
