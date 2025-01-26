package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    TypedQuery<Booking> query;

//    @BeforeEach
//    public void beforeEach() {
//        userService = new UserServiceImpl();
//        itemService = new ItemServiceImpl();
//        bookingService = new BookingServiceImpl();
//    }

    @Test
    void createBookingTest() throws NotFoundException, DuplicateException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");

        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");

        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);

        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        BookingDto saveBooking = bookingService.create(bookingRequestDto, saveBooker.getId());

        query = em.createQuery("Select b from Booking b", Booking.class);
        Booking booking = query.getSingleResult();

        assertThat(booking.getItem().getId(), equalTo(saveItem.getId()));
        assertThat(booking.getBooker().getId(), equalTo(saveBooker.getId()));
    }

    @Test
    void updateBookingTest_Approved() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");
        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        BookingDto saveBooking = bookingService.create(bookingRequestDto, saveBooker.getId());

        BookingDto updatedBooking = bookingService.update(saveBooking.getId(), saveUser.getId(), true);

        assertThat(updatedBooking.getStatus(), equalTo(BookStatus.APPROVED));
    }

    @Test
    void updateBookingTest_Rejected() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");
        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        BookingDto saveBooking = bookingService.create(bookingRequestDto, saveBooker.getId());

        BookingDto updatedBooking = bookingService.update(saveBooking.getId(), saveUser.getId(), false);

        assertThat(updatedBooking.getStatus(), equalTo(BookStatus.REJECTED));
    }

    @Test
    void updateBookingTest_NotOwner() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");
        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        BookingDto saveBooking = bookingService.create(bookingRequestDto, saveBooker.getId());

        UserDto anotherUserDto = new UserDto();
        anotherUserDto.setName("Jane");
        anotherUserDto.setEmail("Jane@email.ru");
        UserDto saveAnotherUser = userService.create(anotherUserDto);

        assertThrows(ValidationException.class, () -> {
            bookingService.update(saveBooking.getId(), saveAnotherUser.getId(), true);
        });
    }

    @Test
    void getBookingByUserIdTest() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");
        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());
        BookingDto saveBooking = bookingService.create(bookingRequestDto, saveBooker.getId());

        BookingDto bookingByUser = bookingService.getBookingById(saveBooking.getId(), saveBooker.getId());

        assertThat(bookingByUser.getItem().getId(), equalTo(saveItem.getId()));
        assertThat(bookingByUser.getBooker().getId(), equalTo(saveBooker.getId()));
    }

    @Test
    void getBookingByUserIdTest_NotFound() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");
        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(999L, saveUser.getId());
        });
    }

    @Test
    void createBookingTest_OwnItem() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingRequestDto, saveUser.getId());
        });
    }

    @Test
    void createBookingTest_ItemNotAvailable() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");
        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(false);

        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        assertThrows(ValidationException.class, () -> {
            bookingService.create(bookingRequestDto, saveBooker.getId());
        });
    }

    @Test
    void createBookingTest_BookOwnItem() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);

        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        assertThrows(NotFoundException.class, () -> {
            bookingService.create(bookingRequestDto, saveUser.getId());
        });
    }


    @Test
    void createBookingTest_InvalidTime() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");
        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);

        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(5));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        assertThrows(ValidationException.class, () -> {
            bookingService.create(bookingRequestDto, saveBooker.getId());
        });
    }

    @Test
    void getBookingByUserIdTest_UserNotOwnerOrBooker() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto saveUser = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("John");
        bookerDto.setEmail("John@email.ru");
        UserDto saveBooker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, saveUser.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        BookingDto saveBooking = bookingService.create(bookingRequestDto, saveBooker.getId());

        UserDto anotherUserDto = new UserDto();
        anotherUserDto.setName("Another User");
        anotherUserDto.setEmail("another@email.ru");
        UserDto saveAnotherUser = userService.create(anotherUserDto);

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(saveBooking.getId(), saveAnotherUser.getId());
        });
    }

    @Test
    void getAllBookingsByBookerIdTest_PastBookings() throws DuplicateException, NotFoundException, ValidationException {
        UserDto bookerDto = new UserDto();
        bookerDto.setName("Oliver");
        bookerDto.setEmail("Oliver@email.ru");
        UserDto booker = userService.create(bookerDto);

        UserDto ownerDto = new UserDto();
        ownerDto.setName("John");
        ownerDto.setEmail("john@email.ru");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, owner.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().minusDays(3));
        bookingRequestDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingRequestDto.setItemId(saveItem.getId());

        BookingDto bookingResponse = bookingService.create(bookingRequestDto, booker.getId());

        List<BookingDto> bookings = bookingService.getBookings("PAST", booker.getId());
        assertEquals(1, bookings.size());
        assertEquals(bookingResponse.getId(), bookings.getFirst().getId());
    }

    @Test
    void getAllBookingsByBookerIdTest_FutureBookings() throws DuplicateException, NotFoundException, ValidationException {
        UserDto bookerDto = new UserDto();
        bookerDto.setName("Oliver");
        bookerDto.setEmail("Oliver@email.ru");
        UserDto booker = userService.create(bookerDto);

        UserDto ownerDto = new UserDto();
        ownerDto.setName("John");
        ownerDto.setEmail("john@email.ru");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto,owner.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        BookingDto bookingResponse = bookingService.create(bookingRequestDto, booker.getId());

        List<BookingDto> bookings = bookingService.getBookings("FUTURE", booker.getId());
        assertEquals(1, bookings.size());
        assertEquals(bookingResponse.getId(), bookings.getFirst().getId());
    }

    @Test
    void getAllBookingsByBookerIdTest_WaitingBookings() throws DuplicateException, NotFoundException, ValidationException {
        UserDto bookerDto = new UserDto();
        bookerDto.setName("Oliver");
        bookerDto.setEmail("Oliver@email.ru");
        UserDto booker = userService.create(bookerDto);

        UserDto ownerDto = new UserDto();
        ownerDto.setName("John");
        ownerDto.setEmail("john@email.ru");
        UserDto owner = userService.create(ownerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("TestDescription");
        itemDto.setAvailable(true);
        ItemDto saveItem = itemService.create(itemDto, owner.getId());

        BookingInputDto bookingRequestDto = new BookingInputDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setItemId(saveItem.getId());

        BookingDto bookingResponse = bookingService.create(bookingRequestDto, booker.getId());
        bookingResponse.setStatus(BookStatus.WAITING);

        List<BookingDto> bookings = bookingService.getBookings("WAITING", booker.getId());
        assertEquals(1, bookings.size());
        assertEquals(bookingResponse.getId(), bookings.getFirst().getId());
    }
}
