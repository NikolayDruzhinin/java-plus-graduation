package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.Repository.UserRepository;
import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.entity.User;
import ru.practicum.main.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("User with email " + newUserRequest.getEmail() + " already exists");
        }

        User user = UserMapper.toUser(newUserRequest);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<User> usersPage;

        if (ids != null && !ids.isEmpty()) {
            usersPage = userRepository.findByIdIn(ids, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.getContent().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> getUsersPage(List<Long> ids, Pageable pageable) {
        Page<User> usersPage;

        if (ids != null && !ids.isEmpty()) {
            usersPage = userRepository.findByIdIn(ids, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.map(UserMapper::toUserDto);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.delete(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public User getEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }
}
