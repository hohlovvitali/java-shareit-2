package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    @Test
    void toCommentDtoTest() {
        User author = new User();
        author.setName("Oliver");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto responseDto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), responseDto.getId());
        assertEquals(comment.getText(), responseDto.getText());
        assertEquals(author.getName(), responseDto.getAuthorName());
        assertEquals(comment.getCreated(), responseDto.getCreated());
    }
}
