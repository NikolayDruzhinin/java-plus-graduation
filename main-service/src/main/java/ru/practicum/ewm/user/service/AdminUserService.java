package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;

    public List<UserDto> getUsersByParams(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return UserMapper.toUserDto(userRepository.findByIdIn(ids, pageable));
    }

    @Transactional
    public UserDto createNewUser(NewUserRequest newUserRequest) {
        checkDuplicateUserByEmail(newUserRequest);
        User user = UserMapper.toUser(newUserRequest);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private void checkDuplicateUserByEmail(NewUserRequest user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Пользователь с email = " + user.getEmail() + " уже существует");
        }
    }
}
