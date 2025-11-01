package ru.practicum.ewm.stats.client.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.practicum.ewm.stats.client.CollectorClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogStatsHitAspect {

    private static final Pattern EVENT_ID_PATTERN = Pattern.compile("/events/(\\d+)");

    private final CollectorClient collectorClient;

    @Around("@annotation(ru.practicum.ewm.stats.client.aop.LogStatsHit)")
    public Object logStatsHit(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String uri = request.getRequestURI();
                String userIdHeader = request.getHeader("X-EWM-USER-ID");

                // Извлекаем eventId из URI, если это запрос к конкретному событию
                java.util.regex.Matcher matcher = EVENT_ID_PATTERN.matcher(uri);
                if (matcher.find() && userIdHeader != null) {
                    try {
                        long eventId = Long.parseLong(matcher.group(1));
                        long userId = Long.parseLong(userIdHeader);

                        // Отправляем информацию о просмотре в Collector
                        collectorClient.logView(userId, eventId);
                        log.debug("Logged view action: userId={}, eventId={}, uri={}", userId, eventId, uri);
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse userId or eventId from request: userIdHeader={}, uri={}",
                                userIdHeader, uri);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to log stats hit: {}", e.getMessage(), e);
            // Не прерываем выполнение основного метода в случае ошибки логирования
        }

        return result;
    }
}

