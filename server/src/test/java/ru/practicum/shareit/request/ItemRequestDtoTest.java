package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testUserDto() throws Exception {
        ItemRequestDto itemRequestResponseDto = new ItemRequestDto();
        itemRequestResponseDto.setId(1L);
        itemRequestResponseDto.setDescription("TestDescription");
        itemRequestResponseDto.setCreated(LocalDateTime.now());

        JsonContent<ItemRequestDto> result = json.write(itemRequestResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("TestDescription");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotEmpty();
    }
}
