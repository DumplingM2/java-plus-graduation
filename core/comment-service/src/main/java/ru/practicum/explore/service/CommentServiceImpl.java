package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.mapper.CommentMapper;
import ru.practicum.explore.model.Comment;
import ru.practicum.explore.repository.CommentRepository;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createComment(Long userId, NewCommentDto newCommentDto) {
        Comment comment = commentMapper.toComment(newCommentDto, userId);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public CommentDto updateCommentForEvent(Long commentId, Long userId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        Comment updated = commentMapper.updateComment(comment, updateCommentDto);
        return commentMapper.toCommentDto(commentRepository.save(updated));
    }

    @Override
    public void deleteCommentByIdByOwner(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

    @Override
    public void deleteCommentByIdByAdmin(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId) {
        return commentMapper.toCommentDtoList(commentRepository.findByEventId(eventId));
    }
}
