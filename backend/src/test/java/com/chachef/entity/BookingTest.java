package com.chachef.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void defaultConstructor_hasPendingStatus_andNullsElsewhere() {
        var booking = new Booking();

        assertNull(booking.getBookingId(), "bookingId should be null before persistence");
        assertNull(booking.getUser(), "user should be null by default");
        assertNull(booking.getChef(), "chef should be null by default");
        assertNull(booking.getStart(), "start should be null by default");
        assertNull(booking.getEnd(), "end should be null by default");
        assertNull(booking.getAddress(), "address should be null by default");

        assertEquals("Pending", booking.getStatus(), "status should default to 'Pending'");
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        var user = new User();
        var chef = new Chef();
        var start = LocalDateTime.now().plusDays(1).withNano(0);
        var end   = start.plusHours(2);
        var address = "123 Main St";
        var status  = "CONFIRMED";

        var booking = new Booking(user, chef, start, end, address, status);

        assertSame(user, booking.getUser());
        assertSame(chef, booking.getChef());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(address, booking.getAddress());
        assertEquals(status, booking.getStatus());
    }

    @Test
    void setters_updateFields_andGettersReflect() {
        var booking = new Booking();

        var user = new User();
        var chef = new Chef();
        var start = LocalDateTime.of(2025, 12, 1, 10, 0);
        var end   = LocalDateTime.of(2025, 12, 1, 12, 0);
        var address = "456 Elm Ave";
        var status  = "CANCELLED";

        booking.setUser(user);
        booking.setChef(chef);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setAddress(address);
        booking.setStatus(status);

        assertSame(user, booking.getUser());
        assertSame(chef, booking.getChef());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(address, booking.getAddress());
        assertEquals(status, booking.getStatus());
    }

    @Test
    void bookingId_isManagedByPersistence_andHasNoSetter() throws NoSuchMethodException {
        var booking = new Booking();
        assertThrows(NoSuchMethodException.class,
                () -> Booking.class.getMethod("setBookingId", java.util.UUID.class));
        assertNull(booking.getBookingId(), "bookingId should be null until JPA sets it");
    }
}
