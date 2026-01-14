package ru.practicum.application.user.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.api.exception.NotFoundException;

public interface InnerUserInterface {
    @GetMapping("/inner/user/{userId}")
    UserDto getById(@PathVariable Long userId) throws NotFoundException;

    @GetMapping("/inner/user/{userId}/exist")
    boolean existsById(@PathVariable Long userId);
}
