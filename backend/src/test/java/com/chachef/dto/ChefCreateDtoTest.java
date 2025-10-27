package com.chachef.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChefCreateDtoTest {
    @Test
    void constructor() {
        UUID user = UUID.randomUUID();
        double price = 100.0;
        String listingName = "Chef Name";

        ChefCreateDto chefCreateDto = new ChefCreateDto(user, price, listingName);

        assertEquals(user, chefCreateDto.getUser());
        assertEquals(price, chefCreateDto.getPrice());
        assertEquals(listingName, chefCreateDto.getListingName());
    }

    @Test
    void getUser() {
        UUID user = UUID.randomUUID();
        double price = 100.0;
        String listingName = "Chef Name";

        ChefCreateDto chefCreateDto = new ChefCreateDto(user, price, listingName);

        assertEquals(user, chefCreateDto.getUser());
    }

    @Test
    void getPrice() {
        UUID user = UUID.randomUUID();
        double price = 100.0;
        String listingName = "Chef Name";

        ChefCreateDto chefCreateDto = new ChefCreateDto(user, price, listingName);

        assertEquals(price, chefCreateDto.getPrice());
    }

    @Test
    void getListingName() {
        UUID user = UUID.randomUUID();
        double price = 100.0;
        String listingName = "Chef Name";

        ChefCreateDto chefCreateDto = new ChefCreateDto(user, price, listingName);

        assertEquals(listingName, chefCreateDto.getListingName());
    }
}