package ru.practicum.explorewithme.analyzer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * История взаимодействий пользователей с мероприятиями.
 * Хранит максимальный вес действия пользователя с мероприятием.
 */
@Entity
@Table(name = "user_interactions", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_event", columnNames = {"userId", "eventId"})
}, indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_event_id", columnList = "eventId"),
    @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long eventId;

    /**
     * Максимальный вес действия пользователя с мероприятием.
     * VIEW = 1.0, REGISTER = 2.0, LIKE = 3.0
     */
    @Column(nullable = false)
    private Double maxWeight;

    @Column(nullable = false)
    private Instant updatedAt;
}

