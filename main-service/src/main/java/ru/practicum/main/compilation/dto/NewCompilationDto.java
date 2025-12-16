package ru.practicum.main.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class NewCompilationDto {

    private Set<@NotNull Long> events;

    private boolean pinned;

    @Size(min = 1, max = 50)
    @NotBlank
    private String title;
}
