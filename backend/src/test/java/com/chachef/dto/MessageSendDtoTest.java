package com.chachef.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageSendDtoTest {

    @Test
    void constructor() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";

        MessageSendDto messageSendDto = new MessageSendDto(fromId, toId, message);

        assertEquals(fromId, messageSendDto.getFrom());
        assertEquals(toId, messageSendDto.getTo());
        assertEquals(message, messageSendDto.getMessage());
    }

    @Test
    void getFrom() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";

        MessageSendDto messageSendDto = new MessageSendDto(fromId, toId, message);

        assertEquals(fromId, messageSendDto.getFrom());
    }

    @Test
    void getTo() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";

        MessageSendDto messageSendDto = new MessageSendDto(fromId, toId, message);

        assertEquals(toId, messageSendDto.getTo());
    }

    @Test
    void getMessage() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        String message = "Hello World!";

        MessageSendDto messageSendDto = new MessageSendDto(fromId, toId, message);

        assertEquals(message, messageSendDto.getMessage());
    }
}