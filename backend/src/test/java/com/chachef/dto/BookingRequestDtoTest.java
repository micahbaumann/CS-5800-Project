package com.chachef.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class BookingRequestDtoTest {
    @Test
    void constructor() {
        UUID userId = UUID.randomUUID();
        UUID chefId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        String address = "12345 address street";
        BookingRequestDto bookingRequestDto = new BookingRequestDto(userId, chefId, start, end, address);

        assertEquals(userId, bookingRequestDto.getUserId());
        assertEquals(chefId, bookingRequestDto.getChefId());
        assertEquals(start, bookingRequestDto.getStart());
        assertEquals(end, bookingRequestDto.getEnd());
        assertEquals(address, bookingRequestDto.getAddress());
    }

    @Test
    void getUserId() {
        UUID userId = UUID.randomUUID();
        UUID chefId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        String address = "12345 address street";
        BookingRequestDto bookingRequestDto = new BookingRequestDto(userId, chefId, start, end, address);

        assertEquals(userId, bookingRequestDto.getUserId());
    }

    @Test
    void getChefId() {
        UUID userId = UUID.randomUUID();
        UUID chefId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        String address = "12345 address street";
        BookingRequestDto bookingRequestDto = new BookingRequestDto(userId, chefId, start, end, address);

        assertEquals(chefId, bookingRequestDto.getChefId());
    }

    @Test
    void getStart() {
        UUID userId = UUID.randomUUID();
        UUID chefId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        String address = "12345 address street";
        BookingRequestDto bookingRequestDto = new BookingRequestDto(userId, chefId, start, end, address);

        assertEquals(start, bookingRequestDto.getStart());
    }

    @Test
    void getEnd() {
        UUID userId = UUID.randomUUID();
        UUID chefId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        String address = "12345 address street";
        BookingRequestDto bookingRequestDto = new BookingRequestDto(userId, chefId, start, end, address);

        assertEquals(end, bookingRequestDto.getEnd());
    }

    @Test
    void getAddress() {
        UUID userId = UUID.randomUUID();
        UUID chefId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        String address = "12345 address street";
        BookingRequestDto bookingRequestDto = new BookingRequestDto(userId, chefId, start, end, address);

        assertEquals(address, bookingRequestDto.getAddress());
    }
}