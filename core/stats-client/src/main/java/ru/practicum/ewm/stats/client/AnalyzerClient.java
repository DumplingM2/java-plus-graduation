package ru.practicum.ewm.stats.client;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.grpc.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.grpc.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.grpc.RecommendedEventProto;
import ru.practicum.ewm.stats.grpc.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.grpc.UserPredictionsRequestProto;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerStub;

    /**
     * Получает список рекомендуемых мероприятий для пользователя
     *
     * @param userId идентификатор пользователя
     * @param maxResults максимальное количество результатов
     * @return поток рекомендованных мероприятий
     */
    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        try {
            UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                    .setUserId(userId)
                    .setMaxResults(maxResults)
                    .build();

            Iterator<RecommendedEventProto> iterator = analyzerStub.getRecommendationsForUser(request);
            return asStream(iterator);

        } catch (StatusRuntimeException e) {
            log.error("Failed to get recommendations for user: userId={}, error={}", userId, e.getStatus());
            throw e;
        }
    }

    /**
     * Получает список похожих мероприятий, с которыми пользователь не взаимодействовал
     *
     * @param eventId идентификатор мероприятия
     * @param userId идентификатор пользователя
     * @param maxResults максимальное количество результатов
     * @return поток похожих мероприятий
     */
    public Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        try {
            SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                    .setEventId(eventId)
                    .setUserId(userId)
                    .setMaxResults(maxResults)
                    .build();

            Iterator<RecommendedEventProto> iterator = analyzerStub.getSimilarEvents(request);
            return asStream(iterator);

        } catch (StatusRuntimeException e) {
            log.error("Failed to get similar events: eventId={}, userId={}, error={}",
                    eventId, userId, e.getStatus());
            throw e;
        }
    }

    /**
     * Получает количество взаимодействий для указанных мероприятий
     *
     * @param eventIds список идентификаторов мероприятий
     * @return поток мероприятий с количеством взаимодействий
     */
    public Stream<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        try {
            InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                    .addAllEventIds(eventIds)
                    .build();

            Iterator<RecommendedEventProto> iterator = analyzerStub.getInteractionsCount(request);
            return asStream(iterator);

        } catch (StatusRuntimeException e) {
            log.error("Failed to get interactions count: eventIds={}, error={}", eventIds, e.getStatus());
            throw e;
        }
    }

    /**
     * Получает рейтинг мероприятия (сумму взаимодействий)
     *
     * @param eventId идентификатор мероприятия
     * @return рейтинг мероприятия
     */
    public double getEventRating(long eventId) {
        return getInteractionsCount(List.of(eventId))
                .findFirst()
                .map(RecommendedEventProto::getScore)
                .orElse(0.0);
    }

    /**
     * Получает рейтинги для списка мероприятий
     *
     * @param eventIds список идентификаторов мероприятий
     * @return мапа eventId -> rating
     */
    public java.util.Map<Long, Double> getEventRatings(List<Long> eventIds) {
        return getInteractionsCount(eventIds)
                .collect(java.util.stream.Collectors.toMap(
                        RecommendedEventProto::getEventId,
                        RecommendedEventProto::getScore,
                        (existing, replacement) -> existing
                ));
    }

    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}

