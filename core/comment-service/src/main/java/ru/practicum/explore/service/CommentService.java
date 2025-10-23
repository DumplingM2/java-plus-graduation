package ru.practicum.explore.service;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, NewCommentDto newCommentDto);

    CommentDto updateCommentForEvent(Long commentId, Long userId, UpdateCommentDto updateCommentDto);

    void deleteCommentByIdByOwner(Long userId, Long commentId);

    void deleteCommentByIdByAdmin(Long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId);
}

