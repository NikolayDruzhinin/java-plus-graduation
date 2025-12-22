package ru.practicum.ewm.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.ewm.validation.UpdateGroup;

@Data
public class UpdateCommentDto {

    @NotBlank(groups = UpdateGroup.class)
    @Size(min = 2, max = 1000, message = "Комментарий должен содержать от 2 до 1000 символов", groups = UpdateGroup.class)
    private String text;
}
