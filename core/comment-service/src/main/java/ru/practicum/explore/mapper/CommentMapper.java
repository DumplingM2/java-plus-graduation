package ru.practicum.explore.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.explore.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        if (comment == null) return null;
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .userId(comment.getUserId())
                .eventId(comment.getEventId())
                .createDate(comment.getCreateDate())
                .build();
    }

    public Comment toComment(NewCommentDto dto, Long userId) {
        if (dto == null) return null;
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setUserId(userId);
        comment.setEventId(dto.getEventId());
        return comment;
    }

    public Comment updateComment(Comment comment, UpdateCommentDto dto) {
        if (dto == null) return comment;
        if (dto.getText() != null) {
            comment.setText(dto.getText());
        }
        return comment;
    }

    public List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null) return null;
        return comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }
}

