package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    TypedQuery<Item> query;
    private Long userId;
    private Long itemId;

    @BeforeEach
    void setUp() throws DuplicateException, NotFoundException, ValidationException {
        UserDto userDto = new UserDto();
        userDto.setName("Oliver");
        userDto.setEmail("Oliver@email.ru");
        UserDto savedUser = userService.create(userDto);
        userId = savedUser.getId();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("TestName");
        itemDto.setDescription("TestingDescription");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.create(itemDto, userId);
        itemId = savedItem.getId();
    }

    @Test
    void createItemTest() {
        query = em.createQuery("Select i from Item i", Item.class);
        Item item = query.getSingleResult();

        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo("TestName"));
        assertThat(item.getDescription(), equalTo("TestingDescription"));
        assertThat(item.getOwner().getId(), equalTo(userId));
    }

    @Test
    void getItemByIdTest() throws NotFoundException {
        ItemDto itemDto = itemService.getItemById(itemId, userId);
        assertThat(itemDto.getId(), equalTo(itemId));
        assertThat(itemDto.getName(), equalTo("TestName"));
    }

    @Test
    void updateItemTest() throws NotFoundException {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("UpdatedName");
        itemService.update(updateDto, userId,itemId);

        ItemDto updatedItem = itemService.getItemById(itemId, userId);
        assertThat(updatedItem.getName(), equalTo("UpdatedName"));
    }

    @Test
    void searchItemsTest() {
        List<ItemDto> items = itemService.getItemsBySearchQuery("Test");
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst().getName(), equalTo("TestName"));
    }

    @Test
    void getItemByIdNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(999L, 999L));
    }

    @Test
    void updateItemByNonOwnerTest() throws DuplicateException, ValidationException {
        UserDto anotherUser = new UserDto();
        anotherUser.setName("AnotherUser");
        anotherUser.setEmail("another@email.ru");
        Long anotherUserId = userService.create(anotherUser).getId();

        ItemDto updateDto = new ItemDto();
        updateDto.setName("UnauthorizedUpdate");

        assertThrows(NotFoundException.class, () -> itemService.update(updateDto, anotherUserId, itemId));
    }

    @Test
    void getAllItemsByUserIdTest() throws NotFoundException {
        List<ItemDto> items = itemService.getItemsByOwner(userId);
        assertThat(items.size(), equalTo(1));
        assertThat(items.getFirst().getId(), equalTo(itemId));
        assertThat(items.getFirst().getName(), equalTo("TestName"));
    }

    @Test
    void deleteTest() throws NotFoundException {
        itemService.delete(itemId, userId);

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId, userId));
    }

    @Test
    void deleteTestNotFoundItem() {
        assertThrows(NotFoundException.class, () -> itemService.delete(999L, userId));
    }

    @Test
    void deleteTestNotFoundOwner() throws ValidationException, DuplicateException, NotFoundException {
        UserDto userDto2 = new UserDto();
        userDto2.setName("Oliver");
        userDto2.setEmail("Oliver2@email.ru");
        UserDto savedUser = userService.create(userDto2);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("TestName");
        itemDto2.setDescription("TestingDescription");
        itemDto2.setAvailable(true);
        ItemDto savedItem = itemService.create(itemDto2, savedUser.getId());

        assertThrows(NotFoundException.class, () -> itemService.delete(savedItem.getId(), userId));
    }

    @Test
    void createItemWithRequestIdTest() throws NotFoundException {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(em.find(User.class, userId));
        itemRequest.setDescription("Need this item");
        itemRequest.setCreated(LocalDateTime.now());
        em.persist(itemRequest);

        ItemDto itemDtoWithRequest = new ItemDto();
        itemDtoWithRequest.setName("RequestItem");
        itemDtoWithRequest.setDescription("Description with request");
        itemDtoWithRequest.setAvailable(true);
        itemDtoWithRequest.setRequestId(itemRequest.getId());

        ItemDto createdItem = itemService.create(itemDtoWithRequest, userId);

        assertThat(createdItem.getRequestId(), equalTo(itemRequest.getId()));
    }

    @Test
    void getCommentsByItemIdTest() {
        Comment comment = new Comment();
        comment.setText("Nice item");
        comment.setItem(em.find(Item.class, itemId));
        comment.setAuthor(em.find(User.class, userId));
        comment.setCreated(LocalDateTime.now());
        em.persist(comment);

        List<CommentDto> comments = itemService.getCommentsByItemId(itemId);

        assertThat(comments.size(), equalTo(1));
        assertThat(comments.getFirst().getText(), equalTo("Nice item"));
    }

    @Test
    void getSearchWithEmptyTextTest() {
        List<ItemDto> items = itemService.getItemsBySearchQuery("");
        assertThat(items.size(), equalTo(0));
    }

    @Test
    void createCommentWithoutApprovedBookingTest() throws NotFoundException {
        Booking booking = new Booking();
        booking.setItem(em.find(Item.class, itemId));
        booking.setBooker(em.find(User.class, userId));
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookStatus.REJECTED);
        em.persist(booking);

        CommentDto commentRequestDto = CommentDto.builder()
                .text("Excellent item!")
                .authorName(booking.getBooker().getName())
                .item(itemService.findItemById(userId))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () ->
                itemService.createComment(commentRequestDto, itemId, userId)
        );

        assertThat(exception.getMessage(), equalTo("Данный пользователь вещь не бронировал!"));
    }

    @Test
    void createCommentSuccessTest() throws ValidationException, NotFoundException {
        Booking booking = new Booking();
        booking.setItem(em.find(Item.class, itemId));
        booking.setBooker(em.find(User.class, userId));
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookStatus.APPROVED);
        em.persist(booking);

        CommentDto commentRequestDto = CommentDto.builder()
                .text("Excellent item!")
                .authorName(booking.getBooker().getName())
                .item(itemService.findItemById(userId))
                .build();

        CommentDto createdComment = itemService.createComment(commentRequestDto, userId, itemId);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment savedComment = query.setParameter("id", createdComment.getId()).getSingleResult();

        assertThat(savedComment.getText(), equalTo("Excellent item!"));
        assertThat(savedComment.getItem().getId(), equalTo(itemId));
        assertThat(savedComment.getAuthor().getId(), equalTo(userId));
    }
}
