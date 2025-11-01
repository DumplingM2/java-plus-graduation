package ru.practicum.explorewithme.analyzer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.analyzer.domain.EventSimilarity;

import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    /**
     * Находит коэффициент сходства для пары мероприятий
     */
    @Query("SELECT es FROM EventSimilarity es WHERE " +
           "(es.eventA = :eventA AND es.eventB = :eventB) OR " +
           "(es.eventA = :eventB AND es.eventB = :eventA)")
    Optional<EventSimilarity> findByEventPair(@Param("eventA") Long eventA, @Param("eventB") Long eventB);

    /**
     * Находит все коэффициенты сходства для мероприятия
     * (где мероприятие является либо eventA, либо eventB)
     */
    @Query("SELECT es FROM EventSimilarity es WHERE es.eventA = :eventId OR es.eventB = :eventId " +
           "ORDER BY es.score DESC")
    List<EventSimilarity> findByEventId(@Param("eventId") Long eventId);

    /**
     * Находит коэффициенты сходства для нескольких мероприятий с указанным
     */
    @Query("SELECT es FROM EventSimilarity es WHERE " +
           "(es.eventA = :eventId AND es.eventB IN :otherEventIds) OR " +
           "(es.eventB = :eventId AND es.eventA IN :otherEventIds) " +
           "ORDER BY es.score DESC")
    List<EventSimilarity> findByEventIdAndOthers(@Param("eventId") Long eventId, 
                                                  @Param("otherEventIds") List<Long> otherEventIds);
}

