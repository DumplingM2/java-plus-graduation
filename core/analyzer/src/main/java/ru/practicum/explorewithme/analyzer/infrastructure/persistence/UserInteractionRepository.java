package ru.practicum.explorewithme.analyzer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.analyzer.domain.UserInteraction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    Optional<UserInteraction> findByUserIdAndEventId(Long userId, Long eventId);

    List<UserInteraction> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<UserInteraction> findByUserIdOrderByUpdatedAtDesc(Long userId, org.springframework.data.domain.Pageable pageable);

    /**
     * Проверяет, взаимодействовал ли пользователь с любым из указанных мероприятий
     */
    @Query("SELECT ui.eventId FROM UserInteraction ui WHERE ui.userId = :userId AND ui.eventId IN :eventIds")
    Set<Long> findEventIdsByUserIdAndEventIds(@Param("userId") Long userId, @Param("eventIds") List<Long> eventIds);

    /**
     * Получает суммы максимальных весов для указанных мероприятий
     */
    @Query("SELECT ui.eventId, SUM(ui.maxWeight) FROM UserInteraction ui " +
           "WHERE ui.eventId IN :eventIds GROUP BY ui.eventId")
    List<Object[]> sumMaxWeightsByEventIds(@Param("eventIds") List<Long> eventIds);
}

