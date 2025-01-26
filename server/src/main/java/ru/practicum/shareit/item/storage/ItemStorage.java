package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item) throws ValidationException;

    Item update(Item item) throws ValidationException, NotFoundException;

    Item delete(Long userId) throws ValidationException, NotFoundException;

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> getItemsBySearchQuery(String text);

    void deleteItemsByOwner(Long ownderId);

    Item getItemById(Long itemId) throws NotFoundException;
}
