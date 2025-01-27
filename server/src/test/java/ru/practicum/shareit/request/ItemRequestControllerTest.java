package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemRequestService itemRequestService;

    static final String HEADER = "X-Sharer-User-Id";

    private final User user = new User(1L, "Oliver", "Oliver@mail.ru");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto();

    private final ItemRequestDto itemRequestResponseDto = new ItemRequestDto(
            1L, "TestDescription", UserMapper.toUserDto(user), LocalDateTime.now().plusDays(1), List.of()
    );

    @Test
    void createItemRequestTest() throws Exception {
        itemRequestDto.setDescription("Test");

        when(itemRequestService.create(any(ItemRequestDto.class), anyLong(), any()))
                .thenReturn(itemRequestResponseDto);

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestResponseDto.getRequestor().getId()), Long.class));
    }

    @Test
    void getAllItemRequestsByRequesterIdTest() throws Exception {

        when(itemRequestService.getAllItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestResponseDto));

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequestsExceptUserIdTest() throws Exception {

        when(itemRequestService.getAllItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestResponseDto));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequestByIdTest() throws Exception {

        when(itemRequestService.getItemRequestById(anyLong(), any()))
                .thenReturn(itemRequestResponseDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
