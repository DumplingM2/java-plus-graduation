package ru.practicum.explorewithme.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.analyzer.domain.EventSimilarity;
import ru.practicum.explorewithme.analyzer.infrastructure.persistence.EventSimilarityRepository;
import ru.practicum.ewm.stats.kafka.EventSimilarityAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventSimilarityListener {

    private final EventSimilarityRepository similarityRepository;

    @KafkaListener(
            topics = "${kafka.topic.events-similarity}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "eventSimilarityKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleEventSimilarity(
            @Payload EventSimilarityAvro similarityAvro,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        log.debug("Received event similarity: eventA={}, eventB={}, score={}, timestamp={}",
                similarityAvro.getEventA(), similarityAvro.getEventB(), 
                similarityAvro.getScore(), similarityAvro.getTimestamp());

        try {
            // Упорядочиваем пару (eventA < eventB)
            long eventA = Math.min(similarityAvro.getEventA(), similarityAvro.getEventB());
            long eventB = Math.max(similarityAvro.getEventA(), similarityAvro.getEventB());

            Instant updatedAt = similarityAvro.getTimestamp();

            // Ищем существующую запись или создаем новую
            EventSimilarity similarity = similarityRepository.findByEventPair(eventA, eventB)
                    .orElseGet(() -> {
                        EventSimilarity newSimilarity = new EventSimilarity();
                        newSimilarity.setEventA(eventA);
                        newSimilarity.setEventB(eventB);
                        return newSimilarity;
                    });

            similarity.setScore(similarityAvro.getScore());
            similarity.setUpdatedAt(updatedAt);

            similarityRepository.save(similarity);

            log.debug("Saved event similarity: eventA={}, eventB={}, score={}",
                    eventA, eventB, similarityAvro.getScore());

        } catch (Exception e) {
            log.error("Error processing event similarity: eventA={}, eventB={}, error={}",
                    similarityAvro.getEventA(), similarityAvro.getEventB(), e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
}

