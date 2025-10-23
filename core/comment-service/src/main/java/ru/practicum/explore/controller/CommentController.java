package ru.practicum.explore.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.explore.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Positive Long userId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, newCommentDto);
    }

    @PatchMapping("/user/{userId}/comment/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        return commentService.updateCommentForEvent(commentId, userId, updateCommentDto);
    }

    @DeleteMapping("/user/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByOwner(@PathVariable @Positive Long userId,
                                     @PathVariable Long commentId) {
        commentService.deleteCommentByIdByOwner(userId, commentId);
    }

    @DeleteMapping("/admin/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable @Positive Long commentId) {
        commentService.deleteCommentByIdByAdmin(commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getCommentsByEventId(@PathVariable @Positive Long eventId) {
        return commentService.getCommentsByEventId(eventId);
    }
}

