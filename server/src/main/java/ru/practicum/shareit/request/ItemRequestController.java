package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.service = itemRequestService;
    }

    @ResponseBody
    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(USER_ID) Long requestorId) throws NotFoundException {
        log.info("Получен POST-запрос к эндпоинту: '/requests' " +
                "на создание запроса вещи от пользователя с ID={}", requestorId);
        return service.create(itemRequestDto, requestorId, LocalDateTime.now());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable("requestId") Long itemRequestId, @RequestHeader(USER_ID) Long userId) throws NotFoundException {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запроса с ID={}", itemRequestId);
        return service.getItemRequestById(itemRequestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(USER_ID) Long userId) throws NotFoundException {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запросов пользователя ID={}",
                userId);
        return service.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID) Long userId) throws NotFoundException {
        log.info("Получен GET-запрос к эндпоинту: '/requests/all' от пользователя с ID={} на получение всех запросов",
                userId);
        return service.getAllItemRequests(userId);
    }
}
