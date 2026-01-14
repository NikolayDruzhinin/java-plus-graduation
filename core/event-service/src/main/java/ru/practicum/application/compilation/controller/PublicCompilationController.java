package ru.practicum.application.compilation.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.application.api.dto.compilation.ResponseCompilationDto;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.compilation.api.PublicCompilationInterface;
import ru.practicum.application.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCompilationController implements PublicCompilationInterface {

    final CompilationService compilationService;

    @Override
    public List<ResponseCompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @Override
    public ResponseCompilationDto getCompilationById(Long compId) throws NotFoundException {
        return compilationService.getCompilationById(compId);
    }


}
