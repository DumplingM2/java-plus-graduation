package ru.practicum.explorewithme.aggregator.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.kafka.EventSimilarityAvro;

import java.time.Instant;

@Slf4j
@Component
public class EventSimilarityProducer {

    private final String eventsSimilarityTopic;
    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;

    public EventSimilarityProducer(
        @Value("${kafka.topic.events-similarity}") String eventsSimilarityTopic,
        KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate) {
        this.eventsSimilarityTopic = eventsSimilarityTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventSimilarity(long eventA, long eventB, double score, Instant timestamp) {
        EventSimilarityAvro similarity = EventSimilarityAvro.newBuilder()
            .setEventA(eventA)
            .setEventB(eventB)
            .setScore(score)
            .setTimestamp(timestamp)
            .build();

        log.debug("Sending event similarity: eventA={}, eventB={}, score={}", eventA, eventB, score);

        kafkaTemplate.send(eventsSimilarityTopic, similarity)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Successfully sent event similarity to offset {}",
                        result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send event similarity: {}", ex.getMessage());
                }
            });
    }
}

