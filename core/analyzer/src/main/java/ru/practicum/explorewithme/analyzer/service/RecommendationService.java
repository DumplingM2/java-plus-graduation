package ru.practicum.explorewithme.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.analyzer.domain.EventSimilarity;
import ru.practicum.explorewithme.analyzer.domain.UserInteraction;
import ru.practicum.explorewithme.analyzer.infrastructure.persistence.EventSimilarityRepository;
import ru.practicum.explorewithme.analyzer.infrastructure.persistence.UserInteractionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final EventSimilarityRepository similarityRepository;
    private final UserInteractionRepository interactionRepository;

    private static final int DEFAULT_RECENT_INTERACTIONS = 10;
    private static final int DEFAULT_K_NEIGHBORS = 5;

    /**
     * Получает список похожих мероприятий, с которыми пользователь не взаимодействовал
     */
    @Transactional(readOnly = true)
    public List<EventSimilarity> getSimilarEvents(long eventId, long userId, int maxResults) {
        log.debug("Getting similar events for eventId={}, userId={}, maxResults={}", eventId, userId, maxResults);

        // 1. Получаем похожие мероприятия
        List<EventSimilarity> similarities = similarityRepository.findByEventId(eventId);

        if (similarities.isEmpty()) {
            log.debug("No similarities found for eventId={}", eventId);
            return Collections.emptyList();
        }

        // 2. Получаем список eventId из найденных сходств
        Set<Long> candidateEventIds = similarities.stream()
                .map(sim -> sim.getEventA().equals(eventId) ? sim.getEventB() : sim.getEventA())
                .collect(Collectors.toSet());

        // 3. Исключаем мероприятия, с которыми пользователь уже взаимодействовал
        Set<Long> interactedEventIds = interactionRepository.findEventIdsByUserIdAndEventIds(userId, new ArrayList<>(candidateEventIds));

        // 4. Фильтруем сходства: убираем те, где пользователь взаимодействовал с обоими мероприятиями
        List<EventSimilarity> filteredSimilarities = similarities.stream()
                .filter(sim -> {
                    long otherEventId = sim.getEventA().equals(eventId) ? sim.getEventB() : sim.getEventA();
                    return !interactedEventIds.contains(otherEventId);
                })
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(maxResults)
                .collect(Collectors.toList());

        log.debug("Found {} similar events for eventId={}, userId={}", filteredSimilarities.size(), eventId, userId);
        return filteredSimilarities;
    }

    /**
     * Получает рекомендации для пользователя на основе предсказания оценки
     */
    @Transactional(readOnly = true)
    public List<EventSimilarity> getRecommendationsForUser(long userId, int maxResults) {
        log.debug("Getting recommendations for userId={}, maxResults={}", userId, maxResults);

        // 1. Получаем недавно просмотренные мероприятия пользователя
        List<UserInteraction> recentInteractions = interactionRepository.findByUserIdOrderByUpdatedAtDesc(
                userId, PageRequest.of(0, DEFAULT_RECENT_INTERACTIONS));

        if (recentInteractions.isEmpty()) {
            log.debug("No interactions found for userId={}", userId);
            return Collections.emptyList();
        }

        List<Long> recentEventIds = recentInteractions.stream()
                .map(UserInteraction::getEventId)
                .collect(Collectors.toList());

        // 2. Находим мероприятия, похожие на просмотренные, но с которыми пользователь не взаимодействовал
        Set<Long> candidateEventIds = new HashSet<>();
        for (Long recentEventId : recentEventIds) {
            List<EventSimilarity> similarities = similarityRepository.findByEventId(recentEventId);
            for (EventSimilarity sim : similarities) {
                long otherEventId = sim.getEventA().equals(recentEventId) ? sim.getEventB() : sim.getEventA();
                if (!recentEventIds.contains(otherEventId)) {
                    candidateEventIds.add(otherEventId);
                }
            }
        }

        if (candidateEventIds.isEmpty()) {
            log.debug("No candidate events found for userId={}", userId);
            return Collections.emptyList();
        }

        // 3. Получаем все сходства для кандидатов
        Map<Long, Double> eventScores = new HashMap<>();
        for (Long candidateEventId : candidateEventIds) {
            double predictedScore = calculatePredictedScore(userId, candidateEventId, recentInteractions);
            eventScores.put(candidateEventId, predictedScore);
        }

        // 4. Сортируем по предсказанной оценке и выбираем топ-N
        return eventScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(entry -> {
                    // Создаем временный объект для возврата
                    EventSimilarity result = new EventSimilarity();
                    result.setEventA(entry.getKey());
                    result.setEventB(0L); // Заглушка
                    result.setScore(entry.getValue()); // Используем предсказанную оценку
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Вычисляет предсказанную оценку мероприятия на основе K ближайших соседей
     */
    private double calculatePredictedScore(long userId, long candidateEventId, List<UserInteraction> userInteractions) {
        // 1. Находим K ближайших соседей (максимально похожие мероприятия, с которыми пользователь взаимодействовал)
        List<Long> userEventIds = userInteractions.stream()
                .map(UserInteraction::getEventId)
                .collect(Collectors.toList());

        List<EventSimilarity> similarities = similarityRepository.findByEventIdAndOthers(
                candidateEventId, userEventIds);

        // Берем K ближайших
        List<EventSimilarity> kNearest = similarities.stream()
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(DEFAULT_K_NEIGHBORS)
                .collect(Collectors.toList());

        if (kNearest.isEmpty()) {
            return 0.0;
        }

        // 2. Получаем оценки пользователя для K ближайших
        Map<Long, Double> userRatings = new HashMap<>();
        for (UserInteraction interaction : userInteractions) {
            userRatings.put(interaction.getEventId(), interaction.getMaxWeight());
        }

        // 3. Вычисляем сумму взвешенных оценок и сумму коэффициентов
        double weightedSum = 0.0;
        double similaritySum = 0.0;

        for (EventSimilarity sim : kNearest) {
            long neighborEventId = sim.getEventA().equals(candidateEventId) ? sim.getEventB() : sim.getEventA();
            Double userRating = userRatings.get(neighborEventId);

            if (userRating != null) {
                weightedSum += sim.getScore() * userRating;
                similaritySum += sim.getScore();
            }
        }

        // 4. Вычисляем предсказанную оценку
        if (similaritySum == 0.0) {
            return 0.0;
        }

        return weightedSum / similaritySum;
    }

    /**
     * Получает суммы взаимодействий для указанных мероприятий
     */
    @Transactional(readOnly = true)
    public Map<Long, Double> getInteractionsCounts(List<Long> eventIds) {
        log.debug("Getting interactions count for eventIds={}", eventIds);

        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Object[]> results = interactionRepository.sumMaxWeightsByEventIds(eventIds);

        Map<Long, Double> counts = new HashMap<>();
        for (Object[] result : results) {
            Long eventId = ((Number) result[0]).longValue();
            Double sum = ((Number) result[1]).doubleValue();
            counts.put(eventId, sum);
        }

        // Для мероприятий без взаимодействий возвращаем 0.0
        for (Long eventId : eventIds) {
            counts.putIfAbsent(eventId, 0.0);
        }

        log.debug("Found interactions counts for {} events", counts.size());
        return counts;
    }
}

