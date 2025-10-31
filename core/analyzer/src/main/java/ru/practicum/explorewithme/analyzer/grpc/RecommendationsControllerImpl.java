package ru.practicum.explorewithme.analyzer.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.grpc.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.grpc.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.grpc.RecommendedEventProto;
import ru.practicum.ewm.stats.grpc.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.grpc.UserPredictionsRequestProto;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class RecommendationsControllerImpl extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    @Override
    public void getRecommendationsForUser(
            UserPredictionsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        log.warn("GetRecommendationsForUser not implemented yet");
        responseObserver.onCompleted();
    }

    @Override
    public void getSimilarEvents(
            SimilarEventsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        log.warn("GetSimilarEvents not implemented yet");
        responseObserver.onCompleted();
    }

    @Override
    public void getInteractionsCount(
            InteractionsCountRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GetInteractionsCount called for event_ids: {}", request.getEventIdsList());

        // TODO: Implement actual interactions count retrieval from database
        // For now, return empty results (score = 0) for each requested event
        for (Long eventId : request.getEventIdsList()) {
            RecommendedEventProto response = RecommendedEventProto.newBuilder()
                    .setEventId(eventId)
                    .setScore(0.0)
                    .build();
            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }
}

