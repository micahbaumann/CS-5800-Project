package com.chachef.repository;

import com.chachef.entity.Booking;
import com.chachef.entity.Chef;
import com.chachef.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private EntityManager em;

    private User user;
    private Chef chef;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User("repoUser", "Repository Tester");
        em.persist(user);

        chef = new Chef();
        chef.setListingName("Test Listing");
        chef.setPrice(99.0);
        chef.setUser(user);
        em.persist(chef);

        booking = new Booking(
                user,
                chef,
                LocalDateTime.now().plusDays(1).withNano(0),
                LocalDateTime.now().plusDays(1).withNano(0).plusHours(2),
                "123 Test St",
                "Pending"
        );
        em.persist(booking);

        em.flush();
        em.clear();
    }

    @Test
    void findByBookingId_returnsSavedBooking() {
        Optional<Booking> found = bookingRepository.findByBookingId(booking.getBookingId());
        assertTrue(found.isPresent());
        assertEquals(booking.getBookingId(), found.get().getBookingId());
        assertEquals("123 Test St", found.get().getAddress());
    }

    @Test
    void findByUser_UserId_returnsListForUser() {
        Optional<List<Booking>> result = bookingRepository.findByUserId(user.getUserId());
        assertTrue(result.isPresent());
        assertFalse(result.get().isEmpty());
        assertEquals(booking.getBookingId(), result.get().get(0).getBookingId());
    }

    @Test
    void findByChef_ChefId_returnsListForChef() {
        Optional<List<Booking>> result = bookingRepository.findByChefId(chef.getChefId());
        assertTrue(result.isPresent());
        assertFalse(result.get().isEmpty());
        assertEquals(booking.getBookingId(), result.get().get(0).getBookingId());
    }

    @Test
    void deleteByBookingId_removesRow() {
        UUID id = booking.getBookingId();
        bookingRepository.deleteByBookingId(id);
        assertTrue(bookingRepository.findByBookingId(id).isEmpty());
    }
}
