package ru.practicum.shareit.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import jakarta.validation.Valid;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) throws NotFoundException {
        log.info("Вывод пользователя с ID={}", userId);
        return userService.getUserById(userId);
    }

    @ResponseBody
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) throws ValidationException, DuplicateException {
        log.info("Создание нового пользователя");
        return userService.create(userDto);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) throws ValidationException, DuplicateException, NotFoundException {
        log.info("Обновление пользователя с ID={}", userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) throws ValidationException, NotFoundException {
        log.info("Удаление пользователя с ID={}", userId);
        userService.delete(userId);
    }
}
