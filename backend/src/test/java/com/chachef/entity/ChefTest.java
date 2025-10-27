package com.chachef.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChefTest {
    @Test
    void defaultConstructor() {
        Chef chef = new Chef();

        assertNull(chef.getChefId(), "chefId should be null before persistence");
        assertNull(chef.getUser(), "user should be null by default");
        assertNull(chef.getListingName(), "listingName should be null by default");
        assertEquals(0.0, chef.getPrice(), "price should be 0.0 by default");
    }

    @Test
    void allArgsConstructor() {
        User user = new User();
        String listingName = "listingName";
        double price = 99.99;

        Chef chef = new Chef(user, price, listingName);

        assertSame(user, chef.getUser());
        assertEquals(listingName, chef.getListingName());
        assertEquals(price, chef.getPrice());
    }

    @Test
    void getChefId() {
        Chef chef = new Chef();

        assertNull(chef.getChefId());
    }

    @Test
    void getUser() {
        User user = new User();
        String listingName = "listingName";
        double price = 99.99;

        Chef chef = new Chef(user, price, listingName);

        assertSame(user, chef.getUser());
    }

    @Test
    void setUser() {
        User user = new User();

        Chef chef = new Chef();

        chef.setUser(user);

        assertSame(user, chef.getUser());
    }

    @Test
    void getListingName() {
        User user = new User();
        String listingName = "listingName";
        double price = 99.99;

        Chef chef = new Chef(user, price, listingName);

        assertEquals(listingName, chef.getListingName());
    }

    @Test
    void setListingName() {
        String listingName = "listingName";

        Chef chef = new Chef();

        chef.setListingName(listingName);

        assertEquals(listingName, chef.getListingName());
    }

    @Test
    void getPrice() {
        User user = new User();
        String listingName = "listingName";
        double price = 99.99;

        Chef chef = new Chef(user, price, listingName);

        assertEquals(price, chef.getPrice());
    }

    @Test
    void setPrice() {
        double price = 99.99;

        Chef chef = new Chef();

        chef.setPrice(price);

        assertEquals(price, chef.getPrice());
    }
}