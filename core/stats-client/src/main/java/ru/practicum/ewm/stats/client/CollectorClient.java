package ru.practicum.ewm.stats.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.grpc.ActionTypeProto;
import ru.practicum.ewm.stats.grpc.UserActionControllerGrpc;
import ru.practicum.ewm.stats.grpc.UserActionProto;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorStub;

    /**
     * Отправляет информацию о действии пользователя в Collector сервис
     *
     * @param userId идентификатор пользователя
     * @param eventId идентификатор мероприятия
     * @param actionType тип действия
     */
    public void collectUserAction(long userId, long eventId, ActionTypeProto actionType) {
        collectUserAction(userId, eventId, actionType, Instant.now());
    }

    /**
     * Отправляет информацию о действии пользователя в Collector сервис
     *
     * @param userId идентификатор пользователя
     * @param eventId идентификатор мероприятия
     * @param actionType тип действия
     * @param timestamp временная метка действия
     */
    public void collectUserAction(long userId, long eventId, ActionTypeProto actionType, Instant timestamp) {
        try {
            UserActionProto request = UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(actionType)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(timestamp.getEpochSecond())
                            .setNanos(timestamp.getNano())
                            .build())
                    .build();

            collectorStub.collectUserAction(request);
            log.debug("Successfully sent user action to collector: userId={}, eventId={}, actionType={}",
                    userId, eventId, actionType);

        } catch (StatusRuntimeException e) {
            log.error("Failed to send user action to collector: userId={}, eventId={}, actionType={}, error={}",
                    userId, eventId, actionType, e.getStatus());
            throw e;
        }
    }

    /**
     * Отправляет информацию о просмотре мероприятия
     */
    public void logView(long userId, long eventId) {
        collectUserAction(userId, eventId, ActionTypeProto.ACTION_VIEW, Instant.now());
    }

    /**
     * Отправляет информацию о регистрации на мероприятие
     */
    public void logRegister(long userId, long eventId) {
        collectUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER, Instant.now());
    }

    /**
     * Отправляет информацию о лайке мероприятия
     */
    public void logLike(long userId, long eventId) {
        collectUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE, Instant.now());
    }
}

