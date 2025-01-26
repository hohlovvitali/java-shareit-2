package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User create(User user) throws DuplicateException, ValidationException;

    User update(User user) throws ValidationException, NotFoundException, DuplicateException;

    User delete(Long userId) throws ValidationException, NotFoundException;

    List<User> getUsers();

    User getUserById(Long userId) throws NotFoundException;
}
