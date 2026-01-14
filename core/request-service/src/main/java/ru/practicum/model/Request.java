package ru.practicum.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.request.enums.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "Requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long eventId;

    @Enumerated(EnumType.STRING)
    Status status;

    Long requesterId;

    LocalDateTime created;
}
