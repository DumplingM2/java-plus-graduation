package ru.practicum.ewm.stats.client.analyzer;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class AnalyzerClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerStub;

    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        try {
            UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                    .setUserId(userId)
                    .setMaxResults(maxResults)
                    .build();

            log.debug("Requesting recommendations for user {} with maxResults {}", userId, maxResults);
            Iterator<RecommendedEventProto> iterator = analyzerStub.getRecommendationsForUser(request);
            return asStream(iterator);
        } catch (Exception e) {
            log.error("Failed to get recommendations for user {}", userId, e);
            throw e;
        }
    }

    public Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        try {
            SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                    .setEventId(eventId)
                    .setUserId(userId)
                    .setMaxResults(maxResults)
                    .build();

            log.debug("Requesting similar events for event {} and user {} with maxResults {}", eventId, userId, maxResults);
            Iterator<RecommendedEventProto> iterator = analyzerStub.getSimilarEvents(request);
            return asStream(iterator);
        } catch (Exception e) {
            log.error("Failed to get similar events for event {} and user {}", eventId, userId, e);
            throw e;
        }
    }

    public List<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        try {
            InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                    .addAllEventIds(eventIds)
                    .build();

            log.debug("Requesting interactions count for events {}", eventIds);
            Iterator<RecommendedEventProto> iterator = analyzerStub.getInteractionsCount(request);
            return asStream(iterator).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get interactions count for events {}", eventIds, e);
            throw e;
        }
    }

    private <T> Stream<T> asStream(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}

