package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    TypedQuery<ItemRequest> query;

    @Test
    void createItemRequestTest() throws DuplicateException, NotFoundException, ValidationException {
        UserDto requesterDto = new UserDto();
        requesterDto.setName("requestor");
        requesterDto.setEmail("requestor@email.ru");
        UserDto requesterUser = userService.create(requesterDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("TestDescription");

        ItemRequestDto createItemRequest = itemRequestService.create(itemRequestDto, requesterUser.getId(), LocalDateTime.now());

        query = em.createQuery("Select r from ItemRequest r", ItemRequest.class);
        ItemRequest itemRequest = query.getSingleResult();

        assertThat(itemRequest.getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(UserMapper.toUserDto(itemRequest.getRequestor()), equalTo(createItemRequest.getRequestor()));
        assertThat(itemRequest.getCreated(), equalTo(createItemRequest.getCreated()));
        assertThat(itemRequest.getId(), equalTo(createItemRequest.getId()));
    }

    @Test
    void createItemRequestSuccessTest() throws NotFoundException, DuplicateException, ValidationException {
        UserDto requesterDto = new UserDto();
        requesterDto.setName("requestor");
        requesterDto.setEmail("requestor@email.ru");
        UserDto requesterUser = userService.create(requesterDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("TestDescription");
        ItemRequestDto createdRequest = itemRequestService.create(itemRequestDto, requesterUser.getId(), LocalDateTime.now());

        query = em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest savedRequest = query.setParameter("id", createdRequest.getId()).getSingleResult();

        assertThat(savedRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(savedRequest.getRequestor().getId(), equalTo(requesterUser.getId()));
        assertThat(savedRequest.getCreated(), equalTo(createdRequest.getCreated()));

        assertThat(createdRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(createdRequest.getRequestor().getId(), equalTo(requesterUser.getId()));
    }

    @Test
    void createItemRequestUserNotFoundTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("TestDescription");

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemRequestService.create(itemRequestDto,999L,  null)
        );

        assertThat(exception.getMessage(), equalTo("Пользователь с ID=999 не найден!"));
    }

    @Test
    void getAllItemRequestsByRequestorIdTest() throws DuplicateException, NotFoundException, ValidationException {
        UserDto requesterDto = new UserDto();
        requesterDto.setName("requestor");
        requesterDto.setEmail("requestor@email.ru");
        UserDto requesterUser = userService.create(requesterDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("TestDescription");
        itemRequestService.create(itemRequestDto, requesterUser.getId(), LocalDateTime.now());

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("TestDescription 2");
        itemRequestService.create(itemRequestDto2, requesterUser.getId(), LocalDateTime.now());

        var requests = itemRequestService.getOwnItemRequests(requesterUser.getId());

        assertThat(requests.size(), equalTo(2));
    }

    @Test
    void getAllItemRequestsExceptUserIdTest() throws DuplicateException, NotFoundException, ValidationException {
        UserDto requesterDto = new UserDto();
        requesterDto.setName("requestor");
        requesterDto.setEmail("requestor@email.ru");
        UserDto requesterUser = userService.create(requesterDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("TestDescription");
        itemRequestService.create(itemRequestDto, requesterUser.getId(), LocalDateTime.now());

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("TestDescription 2");
        itemRequestService.create(itemRequestDto2, requesterUser.getId(), LocalDateTime.now());

        UserDto otherUserDto = new UserDto();
        otherUserDto.setName("other user");
        otherUserDto.setEmail("otheruser@email.ru");
        UserDto otherUser = userService.create(otherUserDto);

        var requests = itemRequestService.getAllItemRequests(otherUser.getId());

        assertThat(requests.size(), equalTo(2));
    }

    @Test
    void getItemRequestByIdTest() throws DuplicateException, NotFoundException, ValidationException {
        UserDto requesterDto = new UserDto();
        requesterDto.setName("requestor");
        requesterDto.setEmail("requestor@email.ru");
        UserDto requesterUser = userService.create(requesterDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("TestDescription");
        ItemRequestDto createdRequest = itemRequestService.create(itemRequestDto, requesterUser.getId(), LocalDateTime.now());

        ItemRequestDto itemRequestResponseDto = itemRequestService.getItemRequestById(createdRequest.getId(), requesterUser.getId());

        assertThat(itemRequestResponseDto.getDescription(), equalTo(createdRequest.getDescription()));
        assertThat(itemRequestResponseDto.getRequestor().getId(), equalTo(requesterUser.getId()));
    }

}
