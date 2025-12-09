package com.chachef.dataobjects;

import java.util.UUID;

public class AuthContext {
    private final UUID userId;
    private final String username;
    private final String name;

    public AuthContext(UUID userId, String username, String name) {
        this.userId = userId;
        this.username = username;
        this.name = name;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
}
