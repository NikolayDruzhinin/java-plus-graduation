package ru.practicum.application.user.mapper;

import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.user.model.User;

public class UserMapper {
    public static User mapDtoToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public static UserDto mapUserToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
