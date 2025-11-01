package ru.practicum.explorewithme.analyzer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Коэффициент сходства между двумя мероприятиями.
 * Упорядочиваем пару по возрастанию eventId (eventA < eventB).
 */
@Entity
@Table(name = "event_similarities", indexes = {
    @Index(name = "idx_event_a", columnList = "eventA"),
    @Index(name = "idx_event_b", columnList = "eventB")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventA;

    @Column(nullable = false)
    private Long eventB;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private Instant updatedAt;

}

