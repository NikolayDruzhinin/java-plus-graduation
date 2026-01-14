package ru.practicum.application.compilation.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.application.compilation.api.CommonCompilationInterface;

@FeignClient(name = "compilation-service")
public interface CompilationClient extends CommonCompilationInterface {
}
