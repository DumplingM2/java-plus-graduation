package ru.practicum.ewm.stats.client.autoconfiguration;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.client.StatsClientImpl;
import ru.practicum.ewm.stats.client.aop.LogStatsHitAspect;

@AutoConfiguration
@ConditionalOnClass(GrpcClient.class)
@ComponentScan(basePackages = "ru.practicum.ewm.stats.client.aop")
public class CollectorClientAutoconfiguration {
    // This autoconfiguration class exists to allow Spring Boot to discover
    // gRPC clients that are annotated with @GrpcClient
    // The actual client beans will be created by grpc-client-spring-boot-starter

    @Bean
    @ConditionalOnMissingBean
    public StatsClient statsClient() {
        return new StatsClientImpl();
    }
}

