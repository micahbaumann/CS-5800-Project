package com.chachef.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChefCreateDtoTest {
    @Test
    void constructor() {
        double price = 100.0;
        String listingName = "Chef Name";

        ChefCreateDto chefCreateDto = new ChefCreateDto(price, listingName);

        assertEquals(price, chefCreateDto.getPrice());
        assertEquals(listingName, chefCreateDto.getListingName());
    }

    @Test
    void getPrice() {
        double price = 100.0;
        String listingName = "Chef Name";

        ChefCreateDto chefCreateDto = new ChefCreateDto(price, listingName);

        assertEquals(price, chefCreateDto.getPrice());
    }

    @Test
    void getListingName() {
        double price = 100.0;
        String listingName = "Chef Name";

        ChefCreateDto chefCreateDto = new ChefCreateDto(price, listingName);

        assertEquals(listingName, chefCreateDto.getListingName());
    }
}