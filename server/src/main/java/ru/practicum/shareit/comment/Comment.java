package ru.practicum.shareit.comment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotEmpty
    private String text;
    @ManyToOne()
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
    private LocalDateTime created;
}