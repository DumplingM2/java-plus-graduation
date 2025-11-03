package ru.practicum.explorewithme.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.aggregator.kafka.EventSimilarityProducer;
import ru.practicum.explorewithme.aggregator.service.SimilarityCalculator;
import ru.practicum.ewm.stats.kafka.UserActionAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionListener {

    private final SimilarityCalculator similarityCalculator;
    private final EventSimilarityProducer similarityProducer;

    @KafkaListener(
            topics = "${kafka.topic.user-actions}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserAction(
            @Payload UserActionAvro userAction,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        log.debug("Received user action: userId={}, eventId={}, actionType={}, timestamp={}",
                userAction.getUserId(), userAction.getEventId(), userAction.getActionType(), userAction.getTimestamp());
        
        try {
            long eventId = userAction.getEventId();
            Instant timestamp = userAction.getTimestamp();

            // Обрабатываем действие и получаем множество eventId, для которых изменилась схожесть
            java.util.Set<Long> affectedEvents = similarityCalculator.processUserAction(userAction);

            // Если схожесть не изменилась ни для одной пары, не нужно отправлять события
            if (affectedEvents.isEmpty()) {
                log.debug("No similarity changes for userId={}, eventId={}, skipping similarity messages",
                        userAction.getUserId(), eventId);
                if (acknowledgment != null) {
                    acknowledgment.acknowledge();
                }
                return;
            }

            // Рассчитываем и отправляем сходство только для пар, где схожесть изменилась
            // Отправляем только события со схожестью > 0
            for (Long otherEventId : affectedEvents) {
                double similarity = similarityCalculator.calculateSimilarity(eventId, otherEventId);
                
                // Отправляем только если схожесть > 0 (есть общие пользователи)
                if (similarity > 0.0) {
                    // Упорядочиваем пару (eventA < eventB)
                    long eventA = Math.min(eventId, otherEventId);
                    long eventB = Math.max(eventId, otherEventId);
                    
                    similarityProducer.sendEventSimilarity(eventA, eventB, similarity, timestamp);
                }
            }

            log.debug("Processed user action and calculated similarity for eventId={} with {} affected events",
                    eventId, affectedEvents.size());

        } catch (Exception e) {
            log.error("Error processing user action: userId={}, eventId={}, error={}",
                    userAction.getUserId(), userAction.getEventId(), e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
}

