package ru.practicum.application.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.api.exception.ConflictException;
import ru.practicum.application.api.exception.NotFoundException;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto newUserDto) throws ConflictException;

    UserDto getUserById(Long userId) throws NotFoundException;

    List<UserDto> getUsersByIdList(List<Long> ids, Pageable page);

    void deleteUser(Long userId);

    boolean existById(Long userId);
}
