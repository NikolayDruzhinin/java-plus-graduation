package ru.practicum.service;

import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;
import java.util.Set;

public interface UserService {

    UserDto createUser(UserCreateDto userCreateDto);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUserById(Long id);

    UserShortDto getUserShortById(Long id);

    UserDto getUserById(Long id);

    List<UserShortDto> getUsersShortById(List<Long> initiatorIds);

    List<UserDto> getUsers(List<Long> userIds);
}
