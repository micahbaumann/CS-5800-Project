package com.chachef.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void defaultConstructor() {
        User  user = new User();

        assertNull(user.getUserId(), "userId should be null before persistence");
        assertNull(user.getName(), "username should be null by default");
        assertNull(user.getName(), "name should be null by default");
    }

    @Test
    void allArgsConstructor() {
        String username = "username";
        String name = "name";
        User  user = new User(username, name, "");

        assertEquals(username, user.getUsername());
        assertEquals(name, user.getName());
    }

    @Test
    void setUserId() {
        UUID id = UUID.randomUUID();
        String username = "username";
        String name = "name";
        User  user = new User(username, name, "");

        user.setUserId(id);

        assertEquals(id, user.getUserId());
    }

    @Test
    void getUserId() {
        User  user = new User();

        assertNull(user.getUserId(), "userId should be null before persistence");
    }

    @Test
    void getName() {
        String username = "username";
        String name = "name";
        User  user = new User(username, name, "");

        assertEquals(name, user.getName());
    }

    @Test
    void setName() {
        String name = "name";
        User user = new User();

        user.setName(name);

        assertEquals(name, user.getName());
    }

    @Test
    void getUsername() {
        String username = "username";
        String name = "name";
        User  user = new User(username, name, "");
        assertEquals(username, user.getUsername());
    }

    @Test
    void setUsername() {
        String username = "username";
        User user = new User();
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }
}