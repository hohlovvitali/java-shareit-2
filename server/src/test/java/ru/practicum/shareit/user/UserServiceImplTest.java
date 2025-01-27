package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    TypedQuery<User> query;

    @Test
    void createUserTest() throws DuplicateException, ru.practicum.shareit.exception.ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("oliver@email.ru");

        userService.create(userDto);

        query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void createUserEmptyEmailTest() {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.create(userDto);
        });

        assertThat(exception.getMessage(), equalTo("Email не может быть пустым"));
    }

    @Test
    void createUserDuplicateEmailTest() throws DuplicateException, ru.practicum.shareit.exception.ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("oliver@email.ru");

        userService.create(userDto);

        UserDto duplicateUserDto = new UserDto();
        duplicateUserDto.setName("Oliver");
        duplicateUserDto.setEmail("oliver@email.ru");

        DuplicateException exception = assertThrows(DuplicateException.class, () -> {
            userService.create(duplicateUserDto);
        });

        assertThat(exception.getMessage(), equalTo("Пользователь с E-mail=oliver@email.ru уже существует!"));
    }

    @Test
    void createUserSaveExceptionTest() throws DuplicateException, ru.practicum.shareit.exception.ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("oliver@email.ru");

        userService.create(userDto);

        UserDto anotherUserDto = new UserDto();
        anotherUserDto.setName("Oliver");
        anotherUserDto.setEmail("oliver@email.ru");

        DuplicateException exception = assertThrows(DuplicateException.class, () -> {
            userService.create(anotherUserDto);
        });

        assertThat(exception.getMessage(), equalTo("Пользователь с E-mail=oliver@email.ru уже существует!"));
    }

    @Test
    void updateUserTest() throws DuplicateException, NotFoundException, ru.practicum.shareit.exception.ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("oliver@email.ru");

        UserDto createdUser = userService.create(userDto);

        UserDto updateUserDto = new UserDto();
        updateUserDto.setName("Updated Oliver");
        UserDto updatedUser = userService.update(updateUserDto, createdUser.getId());

        assertThat(updatedUser.getName(), equalTo("Updated Oliver"));
        assertThat(updatedUser.getEmail(), equalTo(createdUser.getEmail()));
    }

    @Test
    void updateUserDuplicateEmailTest() throws DuplicateException, ru.practicum.shareit.exception.ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("oliver@email.ru");

        userService.create(userDto);

        UserDto anotherUserDto = new UserDto();
        anotherUserDto.setName("John");
        anotherUserDto.setEmail("john@email.ru");

        UserDto createdAnotherUser = userService.create(anotherUserDto);

        UserDto updateUserDto = new UserDto();
        updateUserDto.setEmail("oliver@email.ru");

        DuplicateException exception = assertThrows(DuplicateException.class, () -> {
            userService.update(updateUserDto, createdAnotherUser.getId());
        });

        assertThat(exception.getMessage(), equalTo("Пользователь с E-mail=john@email.ru уже существует!"));
    }

    @Test
    void updateUserNotFoundTest() {
        UserDto updateUserDto = new UserDto();
        updateUserDto.setName("Updated Name");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.update(updateUserDto, 999L);
        });

        assertThat(exception.getMessage(), equalTo("Пользователь с ID=999 не найден!"));
    }

    @Test
    void getUserByIdTest() throws DuplicateException, NotFoundException, ru.practicum.shareit.exception.ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("oliver@email.ru");

        UserDto createdUser = userService.create(userDto);

        UserDto foundUser = userService.getUserById(createdUser.getId());

        assertThat(foundUser.getId(), equalTo(createdUser.getId()));
        assertThat(foundUser.getName(), equalTo(createdUser.getName()));
        assertThat(foundUser.getEmail(), equalTo(createdUser.getEmail()));
    }

    @Test
    void getUserByIdNotFoundTest() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        assertThat(exception.getMessage(), equalTo("Пользователь с ID=999 не найден!"));
    }

    @Test
    void getAllUsersTest() throws DuplicateException, ru.practicum.shareit.exception.ValidationException {
        UserDto userDto1 = new UserDto();
        userDto1.setName("Oliver");
        userDto1.setEmail("oliver@email.ru");

        userService.create(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("John");
        userDto2.setEmail("john@email.ru");

        userService.create(userDto2);

        List<UserDto> users = userService.getUsers();

        assertThat(users.size(), equalTo(2));
    }

    @Test
    void deleteUserTest() throws DuplicateException, NotFoundException, ru.practicum.shareit.exception.ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("oliver@email.ru");

        UserDto createdUser = userService.create(userDto);

        userService.delete(createdUser.getId());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserById(createdUser.getId());
        });

        assertThat(exception.getMessage(), equalTo("Пользователь с ID=" + createdUser.getId() + " не найден!"));
    }
}
