package ru.practicum.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto create(NewCompilationDto dto);

    void delete(Long comId);

    CompilationDto update(Long comIp, UpdateCompilationRequest dto);

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long comId);

}
