package ru.practicum.shareit.request.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {

    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public ItemRequestMapper(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                itemService.getItemsByRequestId(itemRequest.getId())
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) throws NotFoundException {
        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                userService.findUserById(requestorId),
                created
        );
    }
}
