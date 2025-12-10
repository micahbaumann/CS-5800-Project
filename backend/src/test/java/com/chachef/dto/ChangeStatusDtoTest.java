package com.chachef.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChangeStatusDtoTest {
    @Test
    void constructor() {
        UUID bookingId = UUID.randomUUID();
        String status = "Approved";

        ChangeStatusDto changeStatusDto = new ChangeStatusDto(bookingId, status);

        assertEquals(bookingId, changeStatusDto.getBookingId());
        assertEquals(status, changeStatusDto.getStatus());
    }

    @Test
    void getBookingId() {
        UUID bookingId = UUID.randomUUID();
        String status = "Approved";

        ChangeStatusDto changeStatusDto = new ChangeStatusDto(bookingId, status);

        assertEquals(bookingId, changeStatusDto.getBookingId());
    }

    @Test
    void getStatus() {
        UUID bookingId = UUID.randomUUID();
        String status = "Approved";

        ChangeStatusDto changeStatusDto = new ChangeStatusDto(bookingId, status);

        assertEquals(status, changeStatusDto.getStatus());
    }
}