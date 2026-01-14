package ru.practicum.application.compilation.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.application.api.dto.compilation.NewCompilationDto;
import ru.practicum.application.api.dto.compilation.ResponseCompilationDto;
import ru.practicum.application.api.exception.NotFoundException;
import ru.practicum.application.api.exception.ValidationException;
import ru.practicum.application.api.request.compilation.UpdateCompilationRequest;

public interface AdminCompilationInterface {
    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseCompilationDto add(@Valid @RequestBody NewCompilationDto compilationDto) throws NotFoundException;

    @PatchMapping("/admin/compilations/{compId}")
    ResponseCompilationDto update(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest compilationDto
    ) throws NotFoundException;

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long compId) throws ValidationException, NotFoundException;
}
