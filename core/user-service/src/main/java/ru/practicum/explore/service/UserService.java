package ru.practicum.explore.service;

import ru.practicum.dto.user.CreateUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserRequest createUserRequest);

    void deleteUser(long userId);

    Collection<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto getUserById(long userId);
}

