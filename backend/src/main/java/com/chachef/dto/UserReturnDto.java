package com.chachef.dto;

import java.util.UUID;

public class UserReturnDto {
    private final UUID userId;
    private final String username;
    private final String name;

    public UserReturnDto(UUID userId, String username, String name) {
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
