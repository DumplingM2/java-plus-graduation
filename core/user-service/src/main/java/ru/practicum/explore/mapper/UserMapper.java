package ru.practicum.explore.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.user.CreateUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.explore.model.User;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(CreateUserRequest request) {
        if (request == null) return null;
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }
}

