package com.chachef.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ChefCreateDto {
    @NotNull
    private final UUID user;

    @NotNull
    private final double price;

    @NotBlank
    private final String listingName;

    public ChefCreateDto(@JsonProperty("user_id") UUID user, @JsonProperty("price") double price, @JsonProperty("listing_name") String listingName) {
        this.user = user;
        this.price = price;
        this.listingName = listingName;
    }

    public UUID getUser() {
        return user;
    }

    public double getPrice() {
        return price;
    }

    public String getListingName() {
        return listingName;
    }
}
