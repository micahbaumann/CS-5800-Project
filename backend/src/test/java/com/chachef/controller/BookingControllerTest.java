package com.chachef.controller;

import com.chachef.dto.BookingRequestDto;
import com.chachef.dto.ChangeStatusDto;
import com.chachef.entity.Booking;
import com.chachef.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@Import(BookingControllerTest.TestConfig.class)
class BookingControllerTest {

    @Configuration(proxyBeanMethods = false)
    static class TestConfig {
        @Bean
        BookingService bookingService() {
            return mock(BookingService.class);
        }
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper; // Boot provides JavaTimeModule

    @Autowired private BookingService bookingService; // the mock from TestConfig

    private UUID userId;
    private UUID chefId;
    private UUID bookingId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        chefId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
        clearInvocations(bookingService);
        reset(bookingService);
    }

    @Test
    @DisplayName("POST /booking/create -> 200 OK and delegates to service")
    void bookingRequest_ok() throws Exception {
        var start = LocalDateTime.now().plusDays(1).withNano(0);
        var end   = start.plusHours(2);

        var payload = """
            {
              "user_id": "%s",
              "chef_id": "%s",
              "start":   "%s",
              "end":     "%s",
              "address": "123 Main St"
            }
            """.formatted(userId, chefId, start, end);

        when(bookingService.bookingRequest(ArgumentMatchers.any(BookingRequestDto.class)))
                .thenReturn(new Booking());

        mockMvc.perform(post("/booking/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).bookingRequest(any(BookingRequestDto.class));
    }

    @Test
    @DisplayName("POST /booking/create with invalid body -> 400 Bad Request")
    void bookingRequest_validationError() throws Exception {
        mockMvc.perform(post("/booking/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }

    @Test
    @DisplayName("GET /booking/list/user/{userId} -> 200 OK")
    void viewBookingsUser_ok() throws Exception {
        when(bookingService.viewBookingsUser(userId)).thenReturn(List.of(new Booking(), new Booking()));

        mockMvc.perform(get("/booking/list/user/{userId}", userId))
                .andExpect(status().isOk());

        verify(bookingService).viewBookingsUser(userId);
    }

    @Test
    @DisplayName("GET /booking/list/chef/{chefId} -> 200 OK")
    void viewBookingsChef_ok() throws Exception {
        when(bookingService.viewBookingsChef(chefId)).thenReturn(List.of());

        mockMvc.perform(get("/booking/list/chef/{chefId}", chefId))
                .andExpect(status().isOk());

        verify(bookingService).viewBookingsChef(chefId);
    }

    @Test
    @DisplayName("GET /booking/view/{bookingId} -> 200 OK")
    void viewBooking_ok() throws Exception {
        when(bookingService.viewBooking(bookingId)).thenReturn(new Booking());

        mockMvc.perform(get("/booking/view/{bookingId}", bookingId))
                .andExpect(status().isOk());

        verify(bookingService).viewBooking(bookingId);
    }

    @Test
    @DisplayName("PUT /booking/update-status -> 201 Created")
    void changeStatus_created() throws Exception {
        var payload = """
            { "bookingId": "%s", "status": "CONFIRMED" }
            """.formatted(bookingId);

        doNothing().when(bookingService).changeStatus(any(ChangeStatusDto.class));

        mockMvc.perform(put("/booking/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        verify(bookingService).changeStatus(any(ChangeStatusDto.class));
    }

    @Test
    @DisplayName("DELETE /booking/delete/{bookingId} -> 201 Created")
    void deleteBooking_created() throws Exception {
        doNothing().when(bookingService).deleteBooking(bookingId);

        mockMvc.perform(delete("/booking/delete/{bookingId}", bookingId))
                .andExpect(status().isCreated());

        verify(bookingService).deleteBooking(bookingId);
    }
}
