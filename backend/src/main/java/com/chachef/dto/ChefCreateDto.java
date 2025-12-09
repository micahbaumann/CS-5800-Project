package com.chachef.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ChefCreateDto {
    @NotNull
    private final double price;

    @NotBlank
    private final String listingName;

    public ChefCreateDto(@JsonProperty("price") double price, @JsonProperty("listing_name") String listingName) {
        this.price = price;
        this.listingName = listingName;
    }

    public double getPrice() {
        return price;
    }

    public String getListingName() {
        return listingName;
    }
}
