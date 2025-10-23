package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.mapper.UserMapper;
import ru.practicum.explore.model.User;
import ru.practicum.explore.repository.UserRepository;
import ru.practicum.dto.user.CreateUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(CreateUserRequest createUserRequest) {
        // Проверяем, не существует ли уже пользователь с таким email
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new ru.practicum.exception.ConflictException("User with this email already exists");
        }
        
        User user = userMapper.toUser(createUserRequest);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public Collection<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).getContent()
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        List<UserDto> result = new ArrayList<>();
        userRepository.findAllById(ids).forEach(u -> result.add(userMapper.toUserDto(u)));
        return result;
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserDto(user);
    }
}
