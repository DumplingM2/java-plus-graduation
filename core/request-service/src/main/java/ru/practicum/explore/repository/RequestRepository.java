package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.model.Request;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findByRequesterIdOrderByCreatedDateDesc(long userId);

    Optional<Request> findByRequesterIdAndEventId(long userId, long eventId);

    Collection<Request> findByEventIdAndStatus(long eventId, String status);

    Collection<Request> findByIdInAndEventId(List<Long> ids, long eventId);

    // Additional methods for service implementation
    List<Request> findByRequesterId(Long requesterId);
    
    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);
    
    List<Request> findByEventId(Long eventId);
    
    List<Request> findByIdIn(List<Long> ids);
    
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);
}
