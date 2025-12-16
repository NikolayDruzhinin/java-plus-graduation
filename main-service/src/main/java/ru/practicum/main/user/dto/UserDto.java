package ru.practicum.main.user.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class UserDto {
    @Id
    @EqualsAndHashCode.Include
    private long id;
    @Email
    private final String email;
    @NotBlank
    private final String name;
}