package com.chachef.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageReturnDtoTest {

    @Test
    void constructor() {
        UUID messageId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";
        LocalDateTime dateTime = LocalDateTime.now();

        MessageReturnDto messageReturnDto = new MessageReturnDto(messageId, fromId, toId, message, dateTime);

        assertEquals(messageId, messageReturnDto.getMessageId());
        assertEquals(fromId, messageReturnDto.getFrom());
        assertEquals(toId, messageReturnDto.getTo());
        assertEquals(message, messageReturnDto.getMessage());
        assertEquals(dateTime, messageReturnDto.getTimestamp());
    }

    @Test
    void getMessageId() {
        UUID messageId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";
        LocalDateTime dateTime = LocalDateTime.now();

        MessageReturnDto messageReturnDto = new MessageReturnDto(messageId, fromId, toId, message, dateTime);

        assertEquals(messageId, messageReturnDto.getMessageId());
    }

    @Test
    void getFrom() {
        UUID messageId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";
        LocalDateTime dateTime = LocalDateTime.now();

        MessageReturnDto messageReturnDto = new MessageReturnDto(messageId, fromId, toId, message, dateTime);

        assertEquals(fromId, messageReturnDto.getFrom());
    }

    @Test
    void getTo() {
        UUID messageId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";
        LocalDateTime dateTime = LocalDateTime.now();

        MessageReturnDto messageReturnDto = new MessageReturnDto(messageId, fromId, toId, message, dateTime);

        assertEquals(toId, messageReturnDto.getTo());
    }

    @Test
    void getMessage() {
        UUID messageId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";
        LocalDateTime dateTime = LocalDateTime.now();

        MessageReturnDto messageReturnDto = new MessageReturnDto(messageId, fromId, toId, message, dateTime);

        assertEquals(message, messageReturnDto.getMessage());
    }

    @Test
    void getTimestamp() {
        UUID messageId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";
        LocalDateTime dateTime = LocalDateTime.now();

        MessageReturnDto messageReturnDto = new MessageReturnDto(messageId, fromId, toId, message, dateTime);

        assertEquals(dateTime, messageReturnDto.getTimestamp());
    }
}