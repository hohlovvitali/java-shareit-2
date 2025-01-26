package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_request ")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                  // уникальный идентификатор запроса
    private String description;       // текст запроса, содержащий описание требуемой вещи
    @ManyToOne()
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requestor;           // пользователь, создавший запрос
    private LocalDateTime created;    // дата и время создания запроса
}