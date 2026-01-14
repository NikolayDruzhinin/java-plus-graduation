package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateDto {
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "email must be more than 6 but less than 254 characters.")
    String email;
    @NotBlank
    @Size(min = 2, max = 250, message = "name must be more than 2 but less than 250 characters.")
    String name;
}
