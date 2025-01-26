package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

@Component
public class ItemMapper {
    private final ItemService itemService;
    private final BookingService bookingService;

    @Autowired
    @Lazy
    public ItemMapper(ItemService itemService, BookingService bookingService) {
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequestId() != null ? item.getRequestId() : null)
                .lastBooking(bookingService.getLastBooking(item.getId()))
                .nextBooking(bookingService.getNextBooking(item.getId()))
                .build();
    }

    public ItemDto toItemDtoWithoutDates(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequestId() != null ? item.getRequestId() : null)
                .comments(itemService.getCommentsByItemId(item.getId()))
                .build();
    }

    public static Item toItem(ItemDto item, User owner) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .requestId(item.getRequestId() != null ? item.getRequestId() : null)
                .build();
    }
}
