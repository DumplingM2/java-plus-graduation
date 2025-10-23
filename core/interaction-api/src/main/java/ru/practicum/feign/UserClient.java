package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.user.UserDto;

@FeignClient(name = "user-service", path = "/admin/users", fallback = ru.practicum.feign.fallback.UserClientFallback.class)
public interface UserClient {

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable Long userId);
}