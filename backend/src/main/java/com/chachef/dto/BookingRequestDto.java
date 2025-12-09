package com.chachef.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingRequestDto {
    @NotNull
    private UUID chefId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private String address;

    public BookingRequestDto(@JsonProperty("chef_id") UUID chefId, @JsonProperty("start") LocalDateTime start, @JsonProperty("end") LocalDateTime end, @JsonProperty("address") String address) {
        this.chefId = chefId;
        this.start = start;
        this.end = end;
        this.address = address;
    }

    public UUID getChefId() {return chefId;}

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
