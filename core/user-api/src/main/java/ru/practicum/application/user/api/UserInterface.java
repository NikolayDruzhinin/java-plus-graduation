package ru.practicum.application.user.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.api.exception.ConflictException;

import java.util.List;

public interface UserInterface {
    @GetMapping("/admin/users")
    List<UserDto> getUsersList(@RequestParam(required = false) List<Long> ids,
                               @RequestParam(defaultValue = "0") Integer from,
                               @RequestParam(defaultValue = "10") Integer size);

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody UserDto newUser) throws ConflictException;

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long userId);
}
