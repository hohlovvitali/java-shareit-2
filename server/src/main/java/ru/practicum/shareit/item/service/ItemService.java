package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long id, Long userId) throws NotFoundException;

    Item findItemById(Long id) throws NotFoundException;

    ItemDto create(ItemDto itemDto, Long ownerId) throws NotFoundException;

    List<ItemDto> getItemsByOwner(Long ownerId) throws NotFoundException;

    void delete(Long itemId, Long ownerId) throws NotFoundException;

    List<ItemDto> getItemsBySearchQuery(String text);

    ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) throws NotFoundException;

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) throws ValidationException, NotFoundException;

    List<CommentDto> getCommentsByItemId(Long itemId);

    List<ItemDto> getItemsByRequestId(Long requestId);
}
