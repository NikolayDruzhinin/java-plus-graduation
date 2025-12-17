package ru.practicum.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private boolean pinned;
    private String title;
    private List<EventShortDto> events;
}
