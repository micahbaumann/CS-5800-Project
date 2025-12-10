package com.chachef.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserReturnDtoTest {

    @Test
    void constructor() {
        UUID userId = UUID.randomUUID();
        String username = "username";
        String name = "name";

        UserReturnDto userReturnDto = new UserReturnDto(userId, username, name);

        assertEquals(userId, userReturnDto.getUserId());
        assertEquals(username, userReturnDto.getUsername());
        assertEquals(name, userReturnDto.getName());
    }

    @Test
    void getUserId() {
        UUID userId = UUID.randomUUID();
        String username = "username";
        String name = "name";

        UserReturnDto userReturnDto = new UserReturnDto(userId, username, name);

        assertEquals(userId, userReturnDto.getUserId());
    }

    @Test
    void getUsername() {
        UUID userId = UUID.randomUUID();
        String username = "username";
        String name = "name";

        UserReturnDto userReturnDto = new UserReturnDto(userId, username, name);

        assertEquals(username, userReturnDto.getUsername());
    }

    @Test
    void getName() {
        UUID userId = UUID.randomUUID();
        String username = "username";
        String name = "name";

        UserReturnDto userReturnDto = new UserReturnDto(userId, username, name);

        assertEquals(name, userReturnDto.getName());
    }
}