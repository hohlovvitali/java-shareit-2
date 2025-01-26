package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    @Lazy
    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper,
                              ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.mapper = bookingMapper;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto create(BookingInputDto bookingInputDto, Long bookerId) throws ValidationException, NotFoundException {

        checkUserById(bookerId);

        if (!isAvailableItem(bookingInputDto.getItemId())) {
            throw new ValidationException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования!");
        }

        if (bookingInputDto.getStart().isAfter(bookingInputDto.getEnd())) {
            throw new ValidationException("У вещи с ID=" + bookingInputDto.getItemId() +
                    "дата начала брони после даты окончания");
        }

        Booking booking = mapper.toBooking(bookingInputDto, bookerId);
        if (bookerId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Вещь с ID=" + bookingInputDto.getItemId() +
                    " недоступна для бронирования самим владельцем!");
        }

        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) throws NotFoundException, ValidationException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время бронирования уже истекло!");
        }

        if (booking.getBooker().getId().equals(userId)) {
            if (!approved) {
                booking.setStatus(BookStatus.CANCELED);
                log.info("Пользователь с ID={} отменил бронирование с ID={}", userId, bookingId);
            } else {
                throw new NotFoundException("Подтвердить бронирование может только владелец вещи!");
            }
        } else if ((isItemOwner(booking.getItem().getId(), userId)) &&
                (!booking.getStatus().equals(BookStatus.CANCELED))) {
            if (!booking.getStatus().equals(BookStatus.WAITING)) {
                throw new ValidationException("Решение по бронированию уже принято!");
            }
            if (approved) {
                booking.setStatus(BookStatus.APPROVED);
                log.info("Пользователь с ID={} подтвердил бронирование с ID={}", userId, bookingId);
            } else {
                booking.setStatus(BookStatus.REJECTED);
                log.info("Пользователь с ID={} отклонил бронирование с ID={}", userId, bookingId);
            }
        } else {
            if (booking.getStatus().equals(BookStatus.CANCELED)) {
                throw new ValidationException("Бронирование было отменено!");
            } else {
                throw new ValidationException("Подтвердить бронирование может только владелец вещи!");
            }
        }

        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) throws NotFoundException {
        checkUserById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено!"));
        if (booking.getBooker().getId().equals(userId) || isItemOwner(booking.getItem().getId(), userId)) {
            return mapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Посмотреть данные бронирования может только владелец вещи" +
                    " или бронирующий ее!");
        }
    }

    @Override
    public List<BookingDto> getBookings(String state, Long userId) throws ValidationException, NotFoundException {
        checkUserById(userId);
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        bookings = switch (state) {
            case "ALL" -> bookingRepository.findByBookerId(userId, sortByStartDesc);
            case "CURRENT" -> bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                    LocalDateTime.now(), sortByStartDesc);
            case "PAST" -> bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
            case "FUTURE" ->
                    bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
            case "WAITING" -> bookingRepository.findByBookerIdAndStatus(userId, BookStatus.WAITING, sortByStartDesc);
            case "REJECTED" -> bookingRepository.findByBookerIdAndStatus(userId, BookStatus.REJECTED, sortByStartDesc);
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, Long userId) throws ValidationException, NotFoundException {
        checkUserById(userId);
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        bookings = switch (state) {
            case "ALL" -> bookingRepository.findByItem_Owner_Id(userId, sortByStartDesc);
            case "CURRENT" ->
                    bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                            LocalDateTime.now(), sortByStartDesc);
            case "PAST" ->
                    bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
            case "FUTURE" -> bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(),
                    sortByStartDesc);
            case "WAITING" ->
                    bookingRepository.findByItem_Owner_IdAndStatus(userId, BookStatus.WAITING, sortByStartDesc);
            case "REJECTED" ->
                    bookingRepository.findByItem_Owner_IdAndStatus(userId, BookStatus.REJECTED, sortByStartDesc);
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingShortDto getLastBooking(Long itemId) {
        return mapper.toBookingShortDto(bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId,
                LocalDateTime.now()));
    }

    @Override
    public BookingShortDto getNextBooking(Long itemId) {
        return mapper.toBookingShortDto(bookingRepository.findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId,
                LocalDateTime.now()));
    }

    @Override
    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
        return bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), BookStatus.APPROVED);
    }

    private void checkUserById(Long id) throws NotFoundException {
        UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден!")));
    }

    private boolean isAvailableItem(Long itemId) throws NotFoundException {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"))
                .getAvailable();
    }

    private boolean isItemOwner(Long itemId, Long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .anyMatch(item -> item.getId().equals(itemId));
    }
}
