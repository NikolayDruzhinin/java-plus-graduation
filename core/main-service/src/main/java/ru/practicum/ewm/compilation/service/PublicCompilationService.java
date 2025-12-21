package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    CompilationDto getCompilationById(long id);

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);
}
