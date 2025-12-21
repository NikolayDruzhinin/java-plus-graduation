package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.ewm.validation.CreateGroup;

@Data
public class NewUserRequest {

    @NotBlank(groups = CreateGroup.class)
    @Size(min = 2, max = 250, message = "Длина имени должна быть >= 2 символа и <= 250", groups = CreateGroup.class)
    private String name;

    @NotBlank(groups = CreateGroup.class)
    @Email(groups = CreateGroup.class)
    @Size(min = 6, max = 254, message = "Длина email должна быть >= 6 символа и <= 254", groups = CreateGroup.class)
    private String email;
}
