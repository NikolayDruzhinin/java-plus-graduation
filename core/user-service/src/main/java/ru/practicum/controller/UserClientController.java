package ru.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.UserClient;
import ru.practicum.service.AdminUserService;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("internal/api/users")
public class UserClientController implements UserClient{
    private final AdminUserService adminUserService;

    @Override
    public Optional<User> getUserById(Long userId) throws FeignException {
        log.info("Поступил запрос Get /internal/api/users/{} на получение User с id = {}", userId, userId);
        Optional<User> response = adminUserService.getUser(userId);
        log.info("Сформирован ответ Get /internal/api/users/{} с телом: {}", userId, response);
        return response;
    }

    @Override
    public List<User> getUsersWithIds(List<Long> ids) throws FeignException {
        log.info("Поступил запрос Get /internal/api/users/list на получение пользователей");
        return adminUserService.getUsersWithIds(ids);
    }
}
