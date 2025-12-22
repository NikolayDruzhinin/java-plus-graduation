package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.ewm.validation.UpdateGroup;

import java.util.Set;

@Data
public class UpdateCompilationRequest {

    @Size(min = 1, max = 50, message = "Длина названия должна быть >= 1 символа и <= 50", groups = UpdateGroup.class)
    private String title;

    private Boolean pinned;

    private Set<Long> events;

}
