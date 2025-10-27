package com.chachef.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserCreateDtoTest {
    @Test
    void constructor() {
        String username = "user";
        String name = "name";

        UserCreateDto userCreateDto = new UserCreateDto(username, name);

        assertEquals(username, userCreateDto.getUsername());
        assertEquals(name, userCreateDto.getName());
    }

    @Test
    void getUsername() {
        String username = "user";
        String name = "name";

        UserCreateDto userCreateDto = new UserCreateDto(username, name);

        assertEquals(username, userCreateDto.getUsername());
    }

    @Test
    void getName() {
        String username = "user";
        String name = "name";

        UserCreateDto userCreateDto = new UserCreateDto(username, name);

        assertEquals(name, userCreateDto.getName());
    }
}