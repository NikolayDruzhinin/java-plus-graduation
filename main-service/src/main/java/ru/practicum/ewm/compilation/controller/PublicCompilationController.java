package ru.practicum.ewm.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.PublicCompilationService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Validated
public class PublicCompilationController {
    private final PublicCompilationService publicCompilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("GET-запрос к эндпоинту: '/compilations/{}' на получение compilation", compId);
        CompilationDto response = publicCompilationService.getCompilationById(compId);
        log.info("Сформирован ответ GET '/compilations/{}' с телом: {}", compId, response);
        return response;
    }

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр 'from' не может быть отрицательным") int from,
                                                   @RequestParam(defaultValue = "10") @Positive(message = "Параметр 'size' должен быть больше 0") int size) {
        log.info("GET-запрос к эндпоинту: '/compilations' на получение compilations");
        List<CompilationDto> response = publicCompilationService.getAllCompilations(pinned, from, size);
        log.info("Сформирован ответ GET '/compilations' с телом: {}", response);
        return response;
    }

}
