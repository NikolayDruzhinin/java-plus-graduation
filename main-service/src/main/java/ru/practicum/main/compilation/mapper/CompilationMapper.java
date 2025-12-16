package ru.practicum.main.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.entity.Compilation;
import ru.practicum.main.event.mapper.EventMapper;

@UtilityClass
public class CompilationMapper {
    public static Compilation toEntity(NewCompilationDto dto) {
        if (dto == null) {
            return null;
        }
        return new Compilation(null, dto.getTitle(), dto.isPinned(), null, null);
    }

    public static CompilationDto toDto(Compilation entity) {
        if (entity == null) {
            return null;
        }
        return new CompilationDto(entity.getId(),
                entity.isPinned(),
                entity.getTitle(),
                entity.getEvents().stream().map(EventMapper::toShortDto).toList());
    }
}
