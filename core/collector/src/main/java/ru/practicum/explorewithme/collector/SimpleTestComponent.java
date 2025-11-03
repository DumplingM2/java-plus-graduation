package ru.practicum.explorewithme.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
@Slf4j
public class SimpleTestComponent {
    
    @PostConstruct
    public void init() {
        log.info("SimpleTestComponent initialized in collector package!");
    }
}

