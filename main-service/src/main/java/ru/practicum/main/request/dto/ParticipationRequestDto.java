package ru.practicum.main.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.main.request.entity.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private RequestStatus status;
}
