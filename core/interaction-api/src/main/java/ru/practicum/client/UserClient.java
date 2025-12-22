package ru.practicum.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "user-service", path = "/internal/api/users", contextId = "userServiceClient")
public interface UserClient {
    @GetMapping("/{userId}")
    Optional<User> getUserById(@PathVariable Long userId) throws FeignException;

    @GetMapping("/list")
    List<User> getUsersWithIds(@RequestParam List<Long> ids) throws FeignException;
}
