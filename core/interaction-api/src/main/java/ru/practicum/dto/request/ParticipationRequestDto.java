package ru.practicum.dto.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.request.enums.Status;

import java.time.LocalDateTime;

@Builder
@Data
public class ParticipationRequestDto {
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    Status status;
}
