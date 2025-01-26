package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    User user = new User(1L, "Oliver", "oliver@email.ru");
    User user2 = new User(1L, "Oliver", "oliver@email.ru");
    User user3 = new User(1L, "Ron", "ron@email.ru");

    @Test
    void userModelTest() {
        assertEquals(user, user2);
        assertNotEquals(user, user3);
    }

    @Test
    void testUserConstructorWithFullParams() {
        User userWithDetails = new User(2L, "John", "john@email.com");

        assertNotNull(userWithDetails);
        assertEquals(2L, userWithDetails.getId());
        assertEquals("John", userWithDetails.getName());
        assertEquals("john@email.com", userWithDetails.getEmail());
    }

    @Test
    void testUserConstructorEquality() {
        User user1 = new User(1L, "Jane", "jane@email.com");
        User user2 = new User(1L, "Jane", "jane@email.com");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
