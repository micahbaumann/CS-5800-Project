package com.chachef.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokensTest {

    @Test
    void defaultConstructor_hasPendingStatus_andNullsElsewhere() {
        RefreshTokens refreshTokens = new RefreshTokens();

        assertNull(refreshTokens.getRefreshId());
        assertNull(refreshTokens.getExpires());
        assertNull(refreshTokens.getUser());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        UUID uuid = UUID.randomUUID();
        User user = new User();
        LocalDateTime expires = LocalDateTime.now();

        RefreshTokens refreshTokens = new RefreshTokens(uuid, expires, user);

        assertSame(uuid, refreshTokens.getRefreshId());
        assertSame(expires, refreshTokens.getExpires());
        assertEquals(user, refreshTokens.getUser());
    }

    @Test
    void getRefreshId() {
        UUID uuid = UUID.randomUUID();
        User user = new User();
        LocalDateTime expires = LocalDateTime.now();

        RefreshTokens refreshTokens = new RefreshTokens(uuid, expires, user);

        assertSame(uuid, refreshTokens.getRefreshId());
    }

    @Test
    void getExpires() {
        UUID uuid = UUID.randomUUID();
        User user = new User();
        LocalDateTime expires = LocalDateTime.now();

        RefreshTokens refreshTokens = new RefreshTokens(uuid, expires, user);

        assertSame(expires, refreshTokens.getExpires());
    }

    @Test
    void setExpires() {
        UUID uuid = UUID.randomUUID();
        User user = new User();
        LocalDateTime expires = LocalDateTime.now();
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime expires2 = dateTime.plusHours(2).plusMinutes(30).plusSeconds(15);

        RefreshTokens refreshTokens = new RefreshTokens(uuid, expires, user);
        refreshTokens.setExpires(expires2);

        assertSame(expires2, refreshTokens.getExpires());
    }

    @Test
    void getUser() {
        UUID uuid = UUID.randomUUID();
        User user = new User();
        LocalDateTime expires = LocalDateTime.now();

        RefreshTokens refreshTokens = new RefreshTokens(uuid, expires, user);

        assertEquals(user, refreshTokens.getUser());
    }

    @Test
    void setUser() {
        UUID uuid = UUID.randomUUID();
        User user = new User();
        User user2 = new User();
        LocalDateTime expires = LocalDateTime.now();

        RefreshTokens refreshTokens = new RefreshTokens(uuid, expires, user);
        refreshTokens.setUser(user2);

        assertEquals(user2, refreshTokens.getUser());
    }
}