package com.chachef.dataobjects;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthContextTest {

    @Test
    void constructorAndGettersTest() {
        UUID userId = UUID.randomUUID();
        String username = "alice";
        String name = "Alice Doe";

        AuthContext authContext = new AuthContext(userId, username, name);

        assertEquals(userId, authContext.getUserId());
        assertEquals(username, authContext.getUsername());
        assertEquals(name, authContext.getName());
    }
}
