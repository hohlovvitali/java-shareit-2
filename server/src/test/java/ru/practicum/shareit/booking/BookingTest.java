package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {
    User user = new User(
            1L,
            "Oliver",
            "Oliver@email.ru"
    );

    Item item = new Item(1L,
            "item",
            "item 1 Oh",
            false,
            user,
            1L);

    Booking booking = new Booking(
            1L,
            null,
            null,
            item,
            user,
            BookStatus.WAITING);

    Booking booking2 = new Booking(
            1L,
            null,
            null,
            item,
            user,
            BookStatus.WAITING);

    Booking booking3 = new Booking(
            1L,
            null,
            null,
            item,
            user,
            BookStatus.APPROVED);

    @Test
    void bookingModelTest() {
        assertEquals(booking, booking2);
        assertNotEquals(booking, booking3);
    }

    @Test
    void bookingStateEnumTest() {
        for (BookingState state : BookingState.values()) {
            assertNotNull(state);
        }
    }

    @Test
    void bookingStateEnumValuesTest() {
        assertEquals(BookingState.ALL, BookingState.valueOf("ALL"));
        assertEquals(BookingState.CURRENT, BookingState.valueOf("CURRENT"));
        assertEquals(BookingState.PAST, BookingState.valueOf("PAST"));
        assertEquals(BookingState.FUTURE, BookingState.valueOf("FUTURE"));
        assertEquals(BookingState.WAITING, BookingState.valueOf("WAITING"));
        assertEquals(BookingState.REJECTED, BookingState.valueOf("REJECTED"));
    }

    @Test
    void bookingStateEnumCountTest() {
        assertEquals(6, BookingState.values().length);
    }
}
