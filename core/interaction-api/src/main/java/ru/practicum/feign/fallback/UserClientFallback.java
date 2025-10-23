package ru.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ServiceUnavailableException;
import ru.practicum.feign.UserClient;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDto getUser(Long userId) {
        throw new ServiceUnavailableException("Client-Service is unavailable");
    }
}