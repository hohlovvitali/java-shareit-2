package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    static final String HEADER = "X-Sharer-User-Id";

    BookingInputDto bookingRequestDto = new BookingInputDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3)
    );

    BookingDto bookingResponseDto = new BookingDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3),
            new ItemDto(),
            new UserDto(),
            BookStatus.APPROVED
    );

    @Test
    void createBookingValidTest() throws Exception {
        when(bookingService.create(any(BookingInputDto.class), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateBookingValidTest() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), any()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingByUserIdTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(bookingService).getBookingById(1L, 1L);
    }

    @Test
    void getAllBookingsByBookerIdTest() throws Exception {
        when(bookingService.getBookingsOwner(any(), anyLong()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1L)
                        .param("state", "all"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsByOwnerIdTest() throws Exception {
        when(bookingService.getBookingsOwner(any(), anyLong()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L)
                        .param("state", "all"))
                .andExpect(status().isOk());
    }

    @Test
    void updateBookingNotFoundTest() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), any()))
                .thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookingsByBookerIdWithStateTest() throws Exception {
        when(bookingService.getBookingsOwner(any(), anyLong()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1L)
                        .param("state", "CURRENT")) // Проверка состояния
                .andExpect(status().isOk());
    }
}
