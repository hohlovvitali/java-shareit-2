package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemTest {
    User user = new User(
            1L,
            "Oliver",
            "oliver@email.ru");

    Item item = new Item(
            1L,
            "Item1",
            "Test",
            true,
            user,
            1L);

    Item item2 = new Item(
            1L,
            "Item1",
            "Test",
            true,
            user,
            1L);

    Item item3 = new Item(
            1L,
            "Item1",
            "Test",
            false,
            user,
            1L);

    @Test
    void itemModelTest() {
        assertEquals(item, item2);
        assertNotEquals(item, item3);
    }

    @Test
    void testConstructorWithParameters() {
        Item newItem = new Item(2L, "Item2", "Another Test", true, user, 1L);

        assertEquals(2L, newItem.getId());
        assertEquals("Item2", newItem.getName());
        assertEquals("Another Test", newItem.getDescription());
        assertEquals(true, newItem.getAvailable());
        assertEquals(user, newItem.getOwner());
    }
}
