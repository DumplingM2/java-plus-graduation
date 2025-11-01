package ru.practicum.explorewithme.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.kafka.ActionTypeAvro;
import ru.practicum.ewm.stats.kafka.UserActionAvro;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для расчета косинусного сходства мероприятий.
 * 
 * Формула сходства: similarity(A, B) = S_min(A, B) / sqrt(S_a * S_b)
 * где:
 * - S_min(A, B) - сумма минимальных весов для пользователей, взаимодействовавших с обоими мероприятиями
 * - S_a - сумма всех весов действий с мероприятием A
 * - S_b - сумма всех весов действий с мероприятием B
 */
@Slf4j
@Component
public class SimilarityCalculator {

    // Map<EventId, Map<UserId, MaxWeight>> - максимальный вес действия пользователя с мероприятием
    private final Map<Long, Map<Long, Double>> eventUserWeights = new HashMap<>();

    // Map<EventId, TotalWeightSum> - общая сумма весов всех действий с мероприятием
    private final Map<Long, Double> eventTotalWeights = new HashMap<>();

    // Map<MinEventId, Map<MaxEventId, MinWeightsSum>> - сумма минимальных весов для пар мероприятий
    // Упорядочиваем по возрастанию eventId, чтобы избежать дублирования
    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();

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

    /**
     * Обрабатывает новое действие пользователя и возвращает информацию о том,
     * изменился ли максимальный вес для данного мероприятия
     */
    public boolean processUserAction(UserActionAvro userAction) {
        long userId = userAction.getUserId();
        long eventId = userAction.getEventId();
        double newWeight = getActionWeight(userAction.getActionType());

        Map<Long, Double> userWeights = eventUserWeights.computeIfAbsent(eventId, k -> new HashMap<>());
        Double currentMaxWeight = userWeights.get(userId);

        // Если вес не изменился или уменьшился, не нужно пересчитывать
        if (currentMaxWeight != null && currentMaxWeight >= newWeight) {
            return false;
        }

        // Обновляем максимальный вес пользователя для мероприятия
        double oldWeight = currentMaxWeight != null ? currentMaxWeight : 0.0;
        userWeights.put(userId, newWeight);

        // Обновляем общую сумму весов мероприятия
        eventTotalWeights.put(eventId, 
            eventTotalWeights.getOrDefault(eventId, 0.0) - oldWeight + newWeight);

        // Обновляем суммы минимальных весов для всех пар с этим мероприятием
        updateMinWeights(eventId, userId, oldWeight, newWeight);

        return true;
    }

    /**
     * Обновляет суммы минимальных весов для всех пар мероприятий,
     * содержащих указанное мероприятие
     */
    private void updateMinWeights(long updatedEventId, long userId, double oldWeight, double newWeight) {
        // Проходим по всем мероприятиям
        for (Long otherEventId : eventUserWeights.keySet()) {
            if (otherEventId.equals(updatedEventId)) {
                continue;
            }

            Map<Long, Double> otherUserWeights = eventUserWeights.get(otherEventId);
            if (!otherUserWeights.containsKey(userId)) {
                // Пользователь не взаимодействовал с другим мероприятием
                continue;
            }

            double otherWeight = otherUserWeights.get(userId);
            
            // Упорядочиваем пару по возрастанию eventId
            long first = Math.min(updatedEventId, otherEventId);
            long second = Math.max(updatedEventId, otherEventId);

            Map<Long, Double> minWeightsMap = minWeightsSums.computeIfAbsent(first, k -> new HashMap<>());
            double currentMinSum = minWeightsMap.getOrDefault(second, 0.0);

            // Если oldWeight был 0, значит это первое взаимодействие пользователя с updatedEventId
            // В этом случае просто добавляем новый минимум
            if (oldWeight == 0.0) {
                double newMin = Math.min(newWeight, otherWeight);
                minWeightsMap.put(second, currentMinSum + newMin);
            } else {
                // Обновляем: удаляем старый минимум и добавляем новый
                double oldMin = Math.min(oldWeight, otherWeight);
                double newMin = Math.min(newWeight, otherWeight);
                minWeightsMap.put(second, currentMinSum - oldMin + newMin);
            }
        }
    }

    /**
     * Вычисляет сходство между двумя мероприятиями
     */
    public double calculateSimilarity(long eventA, long eventB) {
        // Упорядочиваем пару
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        double sMin = minWeightsSums
            .getOrDefault(first, new HashMap<>())
            .getOrDefault(second, 0.0);

        double sA = eventTotalWeights.getOrDefault(first, 0.0);
        double sB = eventTotalWeights.getOrDefault(second, 0.0);

        if (sA == 0.0 || sB == 0.0) {
            return 0.0;
        }

        return sMin / Math.sqrt(sA * sB);
    }

    /**
     * Получает все мероприятия, для которых нужно пересчитать сходство
     * (все остальные мероприятия, кроме указанного)
     */
    public java.util.Set<Long> getAllOtherEvents(long eventId) {
        java.util.Set<Long> result = new java.util.HashSet<>(eventUserWeights.keySet());
        result.remove(eventId);
        return result;
    }

    /**
     * Проверяет, является ли мероприятие новым (первое взаимодействие)
     */
    public boolean isNewEvent(long eventId) {
        return !eventUserWeights.containsKey(eventId) || eventUserWeights.get(eventId).isEmpty();
    }
}

