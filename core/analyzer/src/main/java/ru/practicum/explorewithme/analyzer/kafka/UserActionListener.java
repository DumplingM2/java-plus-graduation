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
import ru.practicum.explorewithme.analyzer.domain.UserInteraction;
import ru.practicum.explorewithme.analyzer.infrastructure.persistence.UserInteractionRepository;
import ru.practicum.ewm.stats.kafka.ActionTypeAvro;
import ru.practicum.ewm.stats.kafka.UserActionAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionListener {

    private final UserInteractionRepository interactionRepository;

    /**
     * Получает вес действия по его типу
     */
    private double getActionWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 1.0;
            case REGISTER -> 2.0;
            case LIKE -> 3.0;
        };
    }

    @KafkaListener(
            topics = "${kafka.topic.user-actions}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "userActionKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleUserAction(
            @Payload UserActionAvro userAction,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        log.debug("Received user action: userId={}, eventId={}, actionType={}, timestamp={}",
                userAction.getUserId(), userAction.getEventId(), 
                userAction.getActionType(), userAction.getTimestamp());

        try {
            long userId = userAction.getUserId();
            long eventId = userAction.getEventId();
            double newWeight = getActionWeight(userAction.getActionType());
            Instant timestamp = userAction.getTimestamp();

            // Ищем существующее взаимодействие
            UserInteraction interaction = interactionRepository
                    .findByUserIdAndEventId(userId, eventId)
                    .orElse(UserInteraction.builder()
                            .userId(userId)
                            .eventId(eventId)
                            .maxWeight(0.0)
                            .build());

            // Обновляем только если новый вес больше текущего
            if (newWeight > interaction.getMaxWeight()) {
                interaction.setMaxWeight(newWeight);
                interaction.setUpdatedAt(timestamp);
                interactionRepository.save(interaction);

                log.debug("Updated user interaction: userId={}, eventId={}, maxWeight={}",
                        userId, eventId, newWeight);
            } else {
                log.debug("Weight not increased: userId={}, eventId={}, currentWeight={}, newWeight={}",
                        userId, eventId, interaction.getMaxWeight(), newWeight);
            }

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

