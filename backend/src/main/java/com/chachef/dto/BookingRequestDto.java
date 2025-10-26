package com.chachef.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingRequestDto {
    @NotNull
    private UUID userId;

    @NotNull
    private UUID Id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private String address;

    public BookingRequestDto(@JsonProperty("user_id") UUID userId, @JsonProperty("chef_id") UUID chefId, @JsonProperty("start") LocalDateTime start, @JsonProperty("end") LocalDateTime end, @JsonProperty("address") String address) {
        this.userId = userId;
        this.Id = chefId;
        this.start = start;
        this.end = end;
        this.address = address;
    }

    public UUID getUserId() {
        return userId;
    }

    public void getChefId(UUID id) {
        this.userId = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getAddress() {
        return address;
    }
}
