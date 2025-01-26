package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;
    private Long currentId;

    public InMemoryUserStorage() {
        currentId = 0L;
        users = new HashMap<>();
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) throws DuplicateException, ValidationException {
        if (!checkDuplicateEmail(user.getEmail())) {
            if (isValidUser(user)) {
                if (user.getId() == null) {
                    user.setId(++currentId);
                }

                users.put(user.getId(), user);
            }
        } else {
            throw new DuplicateException("Пользователь с E-mail=" + user.getEmail() + " уже существует!");
        }

        return user;
    }

    @Override
    public User update(User user) throws ValidationException, NotFoundException, DuplicateException {
        if (user.getId() == null) {
            throw new ValidationException("Передан пустой userId!");
        }

        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с ID=" + user.getId() + " не найден!");
        }

        User oldUser = users.get(user.getId());

        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }

        if (user.getEmail() != null) {
            if (checkDuplicateEmail(user.getEmail()) && !oldUser.getEmail().equals(user.getEmail())) {
                throw new DuplicateException("Пользователь с E-mail=" + user.getEmail() + " уже существует!");
            }
        } else {
            user.setEmail(oldUser.getEmail());
        }

        if (isValidUser(user)) {
            users.put(user.getId(), user);
        }

        return user;
    }

    @Override
    public User getUserById(Long userId) throws NotFoundException {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }

        return users.get(userId);
    }

    @Override
    public User delete(Long userId) throws ValidationException, NotFoundException {
        if (userId == null) {
            throw new ValidationException("Передан пустой userId!");
        }

        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }

        return users.remove(userId);
    }

    private boolean checkDuplicateEmail(String emailForCheck) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(emailForCheck));
    }

    private boolean isValidUser(User user) throws ValidationException {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный e-mail пользователя: " + user.getEmail());
        }

        if (user.getName().isEmpty()) {
            throw new ValidationException("Некорректный логин пользователя: " + user.getName());
        }

        return true;
    }
}
