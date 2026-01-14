package ru.practicum.compilation.service;

import ru.practicum.dto.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    CompilationDto getCompilationById(long id);

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);
}
