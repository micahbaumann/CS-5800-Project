package com.chachef.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public class MessageSendDto {
    @NotNull
    private final UUID from;

    @NotNull
    private final UUID to;

    @NotBlank
    private final String message;

    public MessageSendDto(@JsonProperty("from") UUID from, @JsonProperty("to") UUID to, @JsonProperty("message") String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public UUID getFrom() {
        return from;
    }

    public UUID getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }
}
