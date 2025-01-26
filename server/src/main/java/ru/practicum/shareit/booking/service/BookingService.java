package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingInputDto bookingDto, Long bookerId) throws ValidationException, NotFoundException;

    BookingDto update(Long bookingId, Long userId, Boolean approved) throws NotFoundException, ValidationException;

    BookingDto getBookingById(Long bookingId, Long userId) throws NotFoundException;

    List<BookingDto> getBookings(String state, Long userId) throws ValidationException, NotFoundException;

    List<BookingDto> getBookingsOwner(String state, Long userId) throws ValidationException, NotFoundException;

    BookingShortDto getLastBooking(Long itemId);

    BookingShortDto getNextBooking(Long itemId);

    Booking getBookingWithUserBookedItem(Long itemId, Long userId);

}
