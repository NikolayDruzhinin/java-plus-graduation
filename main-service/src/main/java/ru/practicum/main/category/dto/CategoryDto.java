package ru.practicum.main.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name must not be blank")
    @Size(min = 1, max = 50, message = "Category name must be between 1 and 50 characters")
    private String name;
}