package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoTest {

    private final JacksonTester<BookingDto> jacksonTester;

    @Test
    void testBookingDto() throws IOException {
        UserDto user = new UserDto();
        user.setId(1L);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(user);
        bookingDto.setStatus(BookStatus.WAITING);

        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
    }
}
