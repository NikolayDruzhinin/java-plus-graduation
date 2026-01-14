package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.validation.CreateGroup;


@Data
public class NewComment {

    @NotBlank(groups = CreateGroup.class)
    @Size(min = 2, max = 1000, message = "Длина комментария должна быть >= 1 символа и <= 1000", groups = CreateGroup.class)
    private String text;

}
