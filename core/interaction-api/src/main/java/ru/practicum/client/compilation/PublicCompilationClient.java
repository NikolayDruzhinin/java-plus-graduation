package ru.practicum.client.compilation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.MyFeignClientFallback;
import ru.practicum.dto.compilation.CompilationDto;

import java.util.List;

@FeignClient(name = "event-service", fallback = MyFeignClientFallback.class)
public interface PublicCompilationClient {

    @GetMapping("/compilations")
    List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size);

    @GetMapping("/compilations/{compId}")
    CompilationDto getCompilationById(@PathVariable Long compId);
}
