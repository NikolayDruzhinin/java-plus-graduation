package ru.practicum.main.compilation.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.FindCompilationParams;
import ru.practicum.main.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {

    private final CompilationService service;

    @GetMapping("/{compId}")
    public CompilationDto find(@PathVariable @Positive Long compId) {
        return service.findById(compId);
    }

    @GetMapping
    public List<CompilationDto> findAllByParams(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        FindCompilationParams params = new FindCompilationParams(pinned, from, size);
        return service.getAll(params);
    }
}
