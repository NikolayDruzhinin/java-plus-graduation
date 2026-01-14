package ru.practicum.application.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.application.api.dto.compilation.NewCompilationDto;
import ru.practicum.application.api.dto.compilation.ResponseCompilationDto;
import ru.practicum.application.compilation.model.Compilation;

@UtilityClass
public class CompilationMapper {
    public Compilation mapToCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());

        return compilation;
    }

    public ResponseCompilationDto mapToResponseCompilation(Compilation compilation) {
        return ResponseCompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
