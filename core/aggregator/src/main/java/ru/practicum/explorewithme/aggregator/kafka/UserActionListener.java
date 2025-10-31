package ru.practicum.explorewithme.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.kafka.UserActionAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionListener {

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
        log.info("Received user action: userId={}, eventId={}, actionType={}, timestamp={}",
                userAction.getUserId(), userAction.getEventId(), userAction.getActionType(), userAction.getTimestamp());
        
        // TODO: Implement similarity calculation logic here
        
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }
}

