package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestTest {
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
    ItemRequest itemRequest = new ItemRequest(
            1L,
            "Test",
            user,
            null);

    ItemRequest itemRequest2 = new ItemRequest(
            1L,
            "Test",
            user,
            null);
    ItemRequest itemRequest3 = new ItemRequest(
            1L,
            "Test2",
            user,
            null);

    @Test
    void itemRequestHashCodeTest() {
        assertEquals(itemRequest, itemRequest2);
        assertNotEquals(itemRequest, itemRequest3);
    }
}
