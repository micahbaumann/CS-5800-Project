package com.chachef.dataobjects;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class ApplicationJwtBuilder {

    private String type;
    private UUID userId;
    private String username;
    private String name;
    private Key secret;
    private int expire;

    public ApplicationJwtBuilder() {}

    public ApplicationJwtBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ApplicationJwtBuilder setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public ApplicationJwtBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public ApplicationJwtBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ApplicationJwtBuilder setSecret(Key secret) {
        this.secret = secret;
        return this;
    }

    public ApplicationJwtBuilder setExpire(int expire) {
        this.expire = expire;
        return this;
    }

    public String build() {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expire);

        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("username", username)
            .claim("name", name)
            .claim("type", type)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiresAt))
            .setId(UUID.randomUUID().toString())
            .signWith(secret, SignatureAlgorithm.HS256)
            .compact();
    }
}
