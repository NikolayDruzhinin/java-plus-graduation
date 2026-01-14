package ru.practicum.client.compilation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.MyFeignClientFallback;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

@FeignClient(name = "event-service", fallback = MyFeignClientFallback.class)
public interface AdminCompilationClient {

    @PostMapping("/admin/compilations")
    CompilationDto create(@RequestBody NewCompilationDto dto);

    @DeleteMapping("/admin/compilations/{compId}")
    void deleteCompilationById(@PathVariable Long compId);

    @PatchMapping("/admin/compilations/{compId}")
    CompilationDto update(@PathVariable Long compId,
                          @RequestBody UpdateCompilationRequest dto);
}
