package ru.practicum.explorewithme.collector.grpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Slf4j
public class TestComponent {
    
    @PostConstruct
    public void init() {
        log.info("TestComponent initialized!");
    }
}

