package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) throws NotFoundException;

    ItemRequestDto getItemRequestById(Long itemRequestId, Long userId) throws NotFoundException;

    List<ItemRequestDto> getOwnItemRequests(Long requestorId) throws NotFoundException;

    List<ItemRequestDto> getAllItemRequests(Long userId) throws NotFoundException;
}
