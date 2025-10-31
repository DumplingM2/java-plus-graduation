package ru.practicum.ewm.stats.client.autoconfiguration;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@AutoConfiguration
@ConditionalOnClass(GrpcClient.class)
public class CollectorClientAutoconfiguration {
    // This autoconfiguration class exists to allow Spring Boot to discover
    // gRPC clients that are annotated with @GrpcClient
    // The actual client beans will be created by grpc-client-spring-boot-starter
}

