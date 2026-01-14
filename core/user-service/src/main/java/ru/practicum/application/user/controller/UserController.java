package ru.practicum.application.user.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.user.api.UserInterface;
import ru.practicum.application.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController implements UserInterface {

    final UserService userService;

    @Override
    public List<UserDto> getUsersList(List<Long> ids,
                                      Integer from,
                                      Integer size) {
        return userService.getUsersByIdList(ids, PageRequest.of(from, size));
    }

    @Override
    public UserDto addUser(UserDto newUser) throws ConflictException {
        return userService.addUser(newUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }

}
