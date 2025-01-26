package ru.practicum.shareit.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;                // уникальный идентификатор комментария;
    @NotBlank
    private String text;            // содержимое комментария;
    private String authorName;      // имя автора комментария;
    private LocalDateTime created;  // дата создания комментария.
}
