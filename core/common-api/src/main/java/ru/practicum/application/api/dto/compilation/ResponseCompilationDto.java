package ru.practicum.application.api.dto.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.application.api.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ResponseCompilationDto {
    Long id;
    String title;
    Boolean pinned;
    List<EventShortDto> events;
}
