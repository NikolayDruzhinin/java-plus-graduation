package ru.practicum.ewm.category.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.validation.UpdateGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank(groups = UpdateGroup.class)
    @Size(min = 1, max = 50, message = "Длина названия должна быть >= 1 символа и <= 50", groups = UpdateGroup.class)
    private String name;
}
