package com.chachef.service;

import com.chachef.dto.BookingRequestDto;
import com.chachef.dto.ChangeStatusDto;
import com.chachef.entity.Booking;
import com.chachef.entity.Chef;
import com.chachef.entity.User;
import com.chachef.repository.BookingRepository;
import com.chachef.repository.ChefRepository;
import com.chachef.repository.UserRepository;
import com.chachef.service.exceptions.InvalidBookingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private ChefRepository chefRepository;

    @InjectMocks private BookingService bookingService;

    private UUID userId;
    private UUID chefId;
    private UUID bookingId;

    @BeforeEach
    void init() {
        userId = UUID.randomUUID();
        chefId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
    }

    @Test
    void bookingRequest_savesMappedEntity() {
        var start = LocalDateTime.now().plusDays(1).withNano(0);
        var end   = start.plusHours(2);
        var dto   = new BookingRequestDto(userId, chefId, start, end, "123 Main");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new User()));
        when(chefRepository.findByChefId(chefId)).thenReturn(Optional.of(new Chef()));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking saved = bookingService.bookingRequest(dto);
        assertNotNull(saved);

        var captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        var b = captor.getValue();
        assertEquals(start, b.getStart());
        assertEquals(end, b.getEnd());
        assertEquals("123 Main", b.getAddress());
        assertEquals("Pending", b.getStatus()); // per service
        verify(userRepository).findByUserId(userId);
        verify(chefRepository).findByChefId(chefId);
    }

    @Test
    void bookingRequest_throwsWhenUserMissing() {
        var dto = new BookingRequestDto(
                userId, chefId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "addr"
        );
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidBookingException.class, () -> bookingService.bookingRequest(dto));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void bookingRequest_throwsWhenChefMissing() {
        var dto = new BookingRequestDto(
                userId, chefId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "addr"
        );
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new User()));
        when(chefRepository.findByChefId(chefId)).thenReturn(Optional.empty());

        assertThrows(InvalidBookingException.class, () -> bookingService.bookingRequest(dto));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void bookingRequest_throwsWhenStartAfterEnd() {
        var start = LocalDateTime.now().plusDays(1);
        var end   = start.minusHours(1); // invalid
        var dto   = new BookingRequestDto(userId, chefId, start, end, "addr");
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new User()));
        when(chefRepository.findByChefId(chefId)).thenReturn(Optional.of(new Chef()));

        assertThrows(InvalidBookingException.class, () -> bookingService.bookingRequest(dto));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void viewBookingsUser_returnsEmptyWhenNone() {
        when(bookingRepository.findByUser_UserId(userId)).thenReturn(Optional.empty());
        var list = bookingService.viewBookingsUser(userId);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void viewBookingsUser_returnsListWhenPresent() {
        when(bookingRepository.findByUser_UserId(userId))
                .thenReturn(Optional.of(List.of(new Booking(), new Booking())));
        var list = bookingService.viewBookingsUser(userId);
        assertEquals(2, list.size());
    }

    @Test
    void viewBookingsChef_returnsEmptyWhenNone() {
        when(bookingRepository.findByChef_ChefId(chefId)).thenReturn(Optional.empty());
        var list = bookingService.viewBookingsChef(chefId);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void viewBookingsChef_returnsListWhenPresent() {
        when(bookingRepository.findByChef_ChefId(chefId))
                .thenReturn(Optional.of(List.of(new Booking())));
        var list = bookingService.viewBookingsChef(chefId);
        assertEquals(1, list.size());
    }

    @Test
    void viewBooking_returnsBookingWhenFound() {
        var booking = new Booking();
        when(bookingRepository.findByBookingId(bookingId)).thenReturn(Optional.of(booking));
        assertSame(booking, bookingService.viewBooking(bookingId));
    }

    @Test
    void viewBooking_throwsWhenMissing() {
        when(bookingRepository.findByBookingId(bookingId)).thenReturn(Optional.empty());
        assertThrows(InvalidBookingException.class, () -> bookingService.viewBooking(bookingId));
    }

    @Test
    void changeStatus_updatesAndSaves() {
        var booking = new Booking();
        when(bookingRepository.findByBookingId(bookingId)).thenReturn(Optional.of(booking));

        bookingService.changeStatus(new ChangeStatusDto(bookingId, "CONFIRMED"));

        assertEquals("CONFIRMED", booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void changeStatus_throwsWhenMissing() {
        when(bookingRepository.findByBookingId(bookingId)).thenReturn(Optional.empty());
        assertThrows(InvalidBookingException.class,
                () -> bookingService.changeStatus(new ChangeStatusDto(bookingId, "CONFIRMED")));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void deleteBooking_deletesWhenExists() {
        when(bookingRepository.findByBookingId(bookingId)).thenReturn(Optional.of(new Booking()));
        bookingService.deleteBooking(bookingId);
        verify(bookingRepository).deleteByBookingId(bookingId);
    }

    @Test
    void deleteBooking_throwsWhenMissing() {
        when(bookingRepository.findByBookingId(bookingId)).thenReturn(Optional.empty());
        assertThrows(InvalidBookingException.class, () -> bookingService.deleteBooking(bookingId));
        verify(bookingRepository, never()).deleteByBookingId(any());
    }
}
