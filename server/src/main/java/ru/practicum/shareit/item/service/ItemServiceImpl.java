package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final ItemMapper itemMapper;

    @Autowired
    @Lazy
    public ItemServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository,
                           UserRepository userRepository, BookingService bookingService, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) throws NotFoundException {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + id + " не найдена!"));

        return itemMapper.toItemDtoWithoutDates(item);
    }

    @Override
    public Item findItemById(Long id) throws NotFoundException {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + id + " не найдена!"));
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) throws NotFoundException {
        checkUserById(ownerId);
        return itemMapper.toItemDto(itemRepository
                .save(ItemMapper.toItem(itemDto, userRepository.findById(ownerId).orElseThrow())));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) throws NotFoundException {
        checkUserById(ownerId);
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDtoWithoutDates)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(toList());
    }

    @Override
    public void delete(Long itemId, Long ownerId) throws NotFoundException {
        try {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
            if (!item.getOwner().getId().equals(ownerId)) {
                throw new NotFoundException("У пользователя нет такой вещи!");
            }
            itemRepository.deleteById(itemId);
        } catch (EmptyResultDataAccessException | NotFoundException e) {
            throw new NotFoundException("Вещь с ID=" + itemId + " не найдена!");
        }
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            text = text.toLowerCase();
            return itemRepository.getItemsBySearchQuery(text).stream()
                    .map(itemMapper::toItemDto)
                    .collect(toList());
        } else return new ArrayList<>();
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) throws NotFoundException {
        checkUserById(ownerId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена!"));
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи!");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) throws ValidationException, NotFoundException {
        checkUserById(userId);
        Booking booking = bookingService.getBookingWithUserBookedItem(itemId, userId);

        if (booking == null) {
            throw new ValidationException("Данный пользователь вещь не бронировал!");
        }

        System.out.println(booking);

        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .item(booking.getItem())
                .author(booking.getBooker())
                .text(commentDto.getText())
                .build();

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItem_Id(itemId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return itemRepository.findAllByRequestId(requestId,
                        Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    private void checkUserById(Long id) throws NotFoundException {
        UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден!")));
    }
}
