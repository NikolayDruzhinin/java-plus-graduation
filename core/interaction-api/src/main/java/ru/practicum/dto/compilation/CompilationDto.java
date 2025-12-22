package ru.practicum.dto.compilation;

import lombok.Data;
import ru.practicum.dto.event.EventShortDto;


import java.util.Set;

@Data
public class CompilationDto {

    private Long id;

    private String title;

    private Boolean pinned;

    private Set<EventShortDto> events;

}
