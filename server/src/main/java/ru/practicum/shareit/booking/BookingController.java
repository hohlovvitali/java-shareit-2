package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @ResponseBody
    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingInputDto bookingInputDto,
                             @RequestHeader(USER_ID) Long bookerId) throws ValidationException, NotFoundException {
        log.info("Получен запрос на создание бронирования от пользователя с ID={}", bookerId);
        return bookingService.create(bookingInputDto, bookerId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID) Long userId, @RequestParam Boolean approved) throws ValidationException, NotFoundException {
        log.info("Обновление статуса бронирования с ID={}", bookingId);
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) throws NotFoundException {
        log.info("Получение бронирования с ID={}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(USER_ID) Long userId) throws ValidationException, NotFoundException {
        log.info("Вывод списка всех бронирований пользователя с ID={} с параметром STATE={}", userId, state);
        return bookingService.getBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) Long userId) throws ValidationException, NotFoundException {
        log.info("Вывод списка всех бронирований вещей пользователя с ID={} с параметром STATE={}", userId, state);
        return bookingService.getBookingsOwner(state, userId);
    }
}
