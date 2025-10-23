package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventIdOrderByCreateDateDesc(Long eventId);
    
    List<Comment> findByUserIdOrderByCreateDateDesc(Long userId);
    
    // Additional methods for service implementation
    List<Comment> findByEventId(Long eventId);
    
    List<Comment> findByUserId(Long userId);
    
    Optional<Comment> findByIdAndUserId(Long id, Long userId);
}
