package ru.practicum.shareit.comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
