package ru.practicum.ewm.stats.client.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogStatsHitAspect {

    @Around("@annotation(ru.practicum.ewm.stats.client.aop.LogStatsHit)")
    public Object logStatsHit(ProceedingJoinPoint joinPoint) throws Throwable {
        // TODO: Implement statistics logging to collector service via gRPC
        log.debug("LogStatsHit annotation detected on method: {}", joinPoint.getSignature().getName());
        return joinPoint.proceed();
    }
}

