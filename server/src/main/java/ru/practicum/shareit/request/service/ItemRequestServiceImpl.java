package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRequestMapper mapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) throws NotFoundException {
        checkUserById(requestorId);
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto, requestorId, created);
        return mapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequestById(Long itemRequestId, Long userId) throws NotFoundException {
        checkUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID=" + itemRequestId + " не найден!"));
        return mapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long requestorId) throws NotFoundException {
        checkUserById(requestorId);
        return itemRequestRepository.findAllByRequestorId(requestorId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(mapper::toItemRequestDto)
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) throws NotFoundException {
        checkUserById(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);

        return itemRequestList.stream().map(mapper::toItemRequestDto).collect(toList());
    }

    private void checkUserById(Long id) throws NotFoundException {
        UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден!")));
    }
}
