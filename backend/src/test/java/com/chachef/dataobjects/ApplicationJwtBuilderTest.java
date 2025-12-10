package com.chachef.dataobjects;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationJwtBuilderTest {

    @Test
    void build_generatesJwtWithExpectedClaimsAndExpiration() {
        UUID userId = UUID.randomUUID();
        String username = "alice";
        String name = "Alice Doe";
        String type = "access";
        int expireSeconds = 600;

        Key secret = Keys.hmacShaKeyFor("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));

        String token = new ApplicationJwtBuilder()
                .setType(type)
                .setUserId(userId)
                .setUsername(username)
                .setName(name)
                .setSecret(secret)
                .setExpire(expireSeconds)
                .build();

        assertNotNull(token);
        assertFalse(token.isBlank());

        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token);

        Claims claims = jws.getBody();

        assertEquals(userId.toString(), claims.getSubject());
        assertEquals(username, claims.get("username", String.class));
        assertEquals(name, claims.get("name", String.class));
        assertEquals(type, claims.get("type", String.class));

        assertNotNull(claims.getId());
        assertFalse(claims.getId().isBlank());

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(issuedAt));

        long actualDiffSeconds = expiration.toInstant().getEpochSecond() - issuedAt.toInstant().getEpochSecond();

        assertTrue(actualDiffSeconds >= expireSeconds - 2 && actualDiffSeconds <= expireSeconds + 2);
    }

    @Test
    void builder_isFluentAndReusesSameInstance() {
        Key secret = Keys.hmacShaKeyFor("fedcba9876543210fedcba9876543210".getBytes(StandardCharsets.UTF_8));

        ApplicationJwtBuilder builder = new ApplicationJwtBuilder();

        ApplicationJwtBuilder chained = builder
                .setType("refresh")
                .setUserId(UUID.randomUUID())
                .setUsername("bob")
                .setName("Bob Smith")
                .setSecret(secret)
                .setExpire(300);

        assertSame(builder, chained);
    }
}
