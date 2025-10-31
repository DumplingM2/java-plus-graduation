package ru.practicum.explorewithme.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class CollectorApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CollectorApplication.class, args);
        log.info("Application started. Checking for UserActionControllerService bean...");
        try {
            Object bean = context.getBean("userActionControllerService");
            log.info("UserActionControllerService bean found: {}", bean);
        } catch (Exception e) {
            log.warn("UserActionControllerService bean not found: {}", e.getMessage());
        }
        try {
            Object bean = context.getBean(ru.practicum.explorewithme.collector.grpc.UserActionControllerService.class);
            log.info("UserActionControllerService bean found by type: {}", bean);
        } catch (Exception e) {
            log.warn("UserActionControllerService bean not found by type: {}", e.getMessage());
        }
    }
}

