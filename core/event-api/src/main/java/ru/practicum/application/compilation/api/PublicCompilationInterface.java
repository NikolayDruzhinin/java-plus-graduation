package ru.practicum.application.compilation.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.application.api.dto.compilation.ResponseCompilationDto;
import ru.practicum.application.api.exception.NotFoundException;

import java.util.List;

public interface PublicCompilationInterface {
    @GetMapping("/compilations")
    List<ResponseCompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/compilations/{compId}")
    ResponseCompilationDto getCompilationById(@PathVariable Long compId) throws NotFoundException;
}
