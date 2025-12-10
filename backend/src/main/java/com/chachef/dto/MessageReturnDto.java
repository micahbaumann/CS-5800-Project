package com.chachef.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageReturnDto {
    private final UUID messageId;
    private final UUID from;
    private final UUID to;
    private final String message;
    private final LocalDateTime timestamp;

    public MessageReturnDto(UUID messageId, UUID from, UUID to, String message, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = timestamp;
    }

    public UUID getMessageId() {
        return messageId;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
