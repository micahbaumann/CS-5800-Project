package com.chachef.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public class ChangeStatusDto {
    @NotNull
    private UUID bookingId;

    @NotNull
    @Pattern(regexp = "Approved|Denied|Pending", message = "Status must be \"Approved\", \"Denied\", or \"Pending\"")
    private String status;

    public ChangeStatusDto(@JsonProperty("booking_id") UUID bookingId, @JsonProperty("status") String status) {
        this.bookingId = bookingId;
        this.status = status;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public String getStatus() {
        return status;
    }
}
