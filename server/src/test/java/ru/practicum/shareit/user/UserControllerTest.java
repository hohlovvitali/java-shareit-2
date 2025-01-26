package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    private final UserDto userDto = new UserDto(1L, "Oliver", "mail@email.ru");

    @Test
    void createTest() throws Exception {

        when(userService.create(any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateTest() throws Exception {
        when(userService.update(any(UserDto.class), anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {

        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(anyLong());
    }

    @Test
    void getAllUsersTest() throws Exception {
        List<UserDto> users = List.of(userDto);

        when(userService.getUsers())
                .thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(users.size())))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }
}
