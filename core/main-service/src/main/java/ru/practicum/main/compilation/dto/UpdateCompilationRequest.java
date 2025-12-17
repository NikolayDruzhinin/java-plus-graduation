package ru.practicum.main.compilation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateCompilationRequest {

    private Set<@NotNull Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;
}
