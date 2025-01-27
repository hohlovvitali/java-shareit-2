package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUserById(Long id) throws NotFoundException;

    UserDto create(UserDto userDto) throws DuplicateException, ValidationException;

    UserDto update(UserDto userDto, Long id) throws NotFoundException, DuplicateException;

    void delete(Long userId) throws NotFoundException;

    User findUserById(Long id) throws NotFoundException;
}
