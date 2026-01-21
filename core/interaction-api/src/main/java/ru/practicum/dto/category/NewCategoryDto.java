package ru.practicum.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewCategoryDto {

    @NotBlank
    @Size(min = 1, max = 50, message = "Category name must be 1 to 50 characters")
    private String name;
}
