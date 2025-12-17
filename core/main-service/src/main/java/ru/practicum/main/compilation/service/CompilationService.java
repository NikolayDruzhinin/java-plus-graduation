package ru.practicum.main.compilation.service;

import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.FindCompilationParams;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto dto);

    CompilationDto update(UpdateCompilationRequest dto, Long compId);

    void delete(Long compId);

    CompilationDto findById(Long compId);

    List<CompilationDto> getAll(FindCompilationParams params);
}
