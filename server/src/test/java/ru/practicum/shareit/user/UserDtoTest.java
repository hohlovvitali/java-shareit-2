package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final JacksonTester<UserDto> userDtoJson;
    private final JacksonTester<UserDto> userResponseJson;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "Oliver",
                "oliver.1@mail.com"
        );

        JsonContent<UserDto> result = userDtoJson.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Oliver");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("oliver.1@mail.com");
    }

    @Test
    void testUserResponse() throws Exception {
        UserDto userResponse = new UserDto(
                1L,
                "Oliver",
                "oliver.1@mail.com"
        );

        JsonContent<UserDto> result = userResponseJson.write(userResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Oliver");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("oliver.1@mail.com");
    }
}
