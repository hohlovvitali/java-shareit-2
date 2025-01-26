package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long id) throws NotFoundException {
        return UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден!")));
    }


    @Override
    public UserDto create(UserDto userDto) throws DuplicateException, ValidationException {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }

        try {
            checkEmailDuplicates(userDto);
            return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Пользователь с E-mail=" +
                    userDto.getEmail() + " уже существует!");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Long id) throws NotFoundException, DuplicateException {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден!"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if ((userDto.getEmail() != null) && (!userDto.getEmail().equals(user.getEmail()))) {
            if (repository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userDto.getEmail()))
                    .allMatch(u -> u.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new DuplicateException("Пользователь с E-mail=" + user.getEmail() + " уже существует!");
            }

        }

        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public void delete(Long userId) throws NotFoundException {
        try {
            repository.deleteById(userId);
        } catch (Exception e) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
    }

    @Override
    public User findUserById(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден!"));
    }

    private void checkEmailDuplicates(UserDto userDto) throws DuplicateException {
        if (!repository.findByEmail(userDto.getEmail()).isEmpty()) {
            if (!Objects.equals(repository.findByEmail(userDto.getEmail()).getFirst().getId(), userDto.getId())) {
                throw new DuplicateException("Пользователь с E-mail=" +
                        userDto.getEmail() + " уже существует!");
            }
        }
    }
}
