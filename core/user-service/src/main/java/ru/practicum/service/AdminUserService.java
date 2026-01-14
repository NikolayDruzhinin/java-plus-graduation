package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventClient;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ValidationException;

import ru.practicum.repository.UserRepository;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;
    private final EventClient eventClient;

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

    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public List<User> getUsersWithIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }
}
