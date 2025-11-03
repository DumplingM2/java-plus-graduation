package ru.practicum.explorewithme.analyzer.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.analyzer.domain.EventSimilarity;
import ru.practicum.explorewithme.analyzer.service.RecommendationService;
import ru.practicum.ewm.stats.grpc.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.grpc.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.grpc.RecommendedEventProto;
import ru.practicum.ewm.stats.grpc.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.grpc.UserPredictionsRequestProto;

import java.util.List;
import java.util.Map;

@GrpcService
@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationsControllerImpl extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(
            UserPredictionsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GetRecommendationsForUser called for userId={}, maxResults={}", 
                request.getUserId(), request.getMaxResults());

        try {
            List<EventSimilarity> recommendations = recommendationService.getRecommendationsForUser(
                    request.getUserId(), request.getMaxResults());

            for (EventSimilarity similarity : recommendations) {
                RecommendedEventProto response = RecommendedEventProto.newBuilder()
                        .setEventId(similarity.getEventA())
                        .setScore(similarity.getScore())
                        .build();
                responseObserver.onNext(response);
            }

            log.info("Returned {} recommendations for userId={}", recommendations.size(), request.getUserId());
        } catch (Exception e) {
            log.error("Error getting recommendations for userId={}: {}", request.getUserId(), e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to get recommendations: " + e.getMessage())
                    .asException());
            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getSimilarEvents(
            SimilarEventsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GetSimilarEvents called for eventId={}, userId={}, maxResults={}", 
                request.getEventId(), request.getUserId(), request.getMaxResults());

        try {
            List<EventSimilarity> similarities = recommendationService.getSimilarEvents(
                    request.getEventId(), request.getUserId(), request.getMaxResults());

            for (EventSimilarity similarity : similarities) {
                long otherEventId = similarity.getEventA().equals(request.getEventId()) 
                        ? similarity.getEventB() 
                        : similarity.getEventA();

                RecommendedEventProto response = RecommendedEventProto.newBuilder()
                        .setEventId(otherEventId)
                        .setScore(similarity.getScore())
                        .build();
                responseObserver.onNext(response);
            }

            log.info("Returned {} similar events for eventId={}, userId={}", 
                    similarities.size(), request.getEventId(), request.getUserId());
        } catch (Exception e) {
            log.error("Error getting similar events for eventId={}, userId={}: {}", 
                    request.getEventId(), request.getUserId(), e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to get similar events: " + e.getMessage())
                    .asException());
            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getInteractionsCount(
            InteractionsCountRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GetInteractionsCount called for event_ids: {}", request.getEventIdsList());

        try {
            Map<Long, Double> counts = recommendationService.getInteractionsCounts(request.getEventIdsList());

            for (Long eventId : request.getEventIdsList()) {
                double count = counts.getOrDefault(eventId, 0.0);
                RecommendedEventProto response = RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(count)
                        .build();
                responseObserver.onNext(response);
            }

            log.info("Returned interactions count for {} events", counts.size());
        } catch (Exception e) {
            log.error("Error getting interactions count: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to get interactions count: " + e.getMessage())
                    .asException());
            return;
        }

        responseObserver.onCompleted();
    }
}


