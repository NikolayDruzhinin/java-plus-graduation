package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.UserServiceImpl;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody @Valid @NotNull UserCreateDto userCreateDto) {
        log.info("POST /admin/users - создание пользователя: {}", userCreateDto);
        UserDto result = userService.createUser(userCreateDto);
        log.info("Пользователь успешно создан: {}", result);
        return result;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/users - получение списка пользователей, ids={}, from={}, size={}", ids, from, size);
        List<UserDto> result = userService.getUsers(ids, from, size);
        log.info("Найдено {} пользователей", result.size());
        return result;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("DELETE /admin/users/{} - удаление пользователя", userId);
        userService.deleteUserById(userId);
        log.info("Пользователь с id={} успешно удалён", userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("GET /admin/users/{} - получение пользователя по id", userId);
        UserDto result = userService.getUserById(userId);
        log.info("Пользователь найден: {}", result);
        return result;
    }

    @GetMapping("/short")
    @ResponseStatus(HttpStatus.OK)
    public UserShortDto getUserShortById(@RequestParam("userId") Long userId) {
        log.info("GET /admin/users/short?id={} - получение краткой информации о пользователе", userId);
        UserShortDto result = userService.getUserShortById(userId);
        log.info("Краткая информация получена: {}", result);
        return result;
    }
}
