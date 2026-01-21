package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.ConflictPropertyConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapperCustom;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto createUser(UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new ConflictPropertyConstraintException(String.format("Email %s уже зарегистрирован ",
                    userCreateDto.getEmail()));
        }
        final User user = UserMapperCustom.toUser(userCreateDto);
        return UserMapperCustom.toUserDto(userRepository.save(user));
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(0, size + from, Sort.by("id").ascending());
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, pageable).getContent(); /// нужна ли сортировка?
        }
        return users.stream()
                .skip(from)
                .limit(size)
                .map(UserMapperCustom::toUserDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        checkUserId(id);
        return UserMapperCustom.toUserDto(userRepository.findById(id).get());
    }

    public void deleteUserById(Long id) {
        checkUserId(id);
        userRepository.deleteById(id);
    }

    private void checkUserId(long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    @Override
    public UserShortDto getUserShortById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapperCustom.toUserShortDto(user);
    }
}
