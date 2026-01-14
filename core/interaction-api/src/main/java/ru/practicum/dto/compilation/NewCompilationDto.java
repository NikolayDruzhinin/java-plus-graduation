package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.validation.CreateGroup;


import java.util.Set;

@Data
public class NewCompilationDto {

    @NotBlank(groups = CreateGroup.class)
    @Size(min = 1, max = 50, message = "Длина названия должна быть >= 1 символа и <= 50", groups = CreateGroup.class)
    private String title;

    private Boolean pinned = false;

    private Set<Long> events;
}
