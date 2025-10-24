package com.chachef.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCreateDto {
    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username may contain only letters, numbers, and underscores")
    private final String username;

    @NotBlank
    @Size(max = 100)
    private final String name;

    public UserCreateDto(@JsonProperty("username") String username, @JsonProperty("name") String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
}
