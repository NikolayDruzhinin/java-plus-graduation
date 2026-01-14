package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@Getter
public class UpdateCompilationRequest {

    private Set<Long> events;

    private Boolean pinned;

    @Size(max = 50)
    private String title;
}
