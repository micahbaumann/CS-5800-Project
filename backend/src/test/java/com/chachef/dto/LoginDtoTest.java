package com.chachef.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginDtoTest {

    @Test
    void constructor() {
        String username = "username";
        String password = "password";

        LoginDto loginDto = new LoginDto(username, password);

        assertEquals(username, loginDto.getUsername());
        assertEquals(password, loginDto.getPassword());
    }

    @Test
    void getUsername() {
        String username = "username";
        String password = "password";

        LoginDto loginDto = new LoginDto(username, password);

        assertEquals(username, loginDto.getUsername());
    }

    @Test
    void getPassword() {
        String username = "username";
        String password = "password";

        LoginDto loginDto = new LoginDto(username, password);

        assertEquals(password, loginDto.getPassword());
    }
}