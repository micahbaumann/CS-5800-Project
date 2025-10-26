package com.chachef.service;

import com.chachef.dto.BookingRequestDto;
import com.chachef.dto.ChangeStatusDto;
import com.chachef.entity.Booking;
import com.chachef.repository.BookingRepository;
import com.chachef.service.exceptions.InvalidBookingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public Booking bookingRequest(BookingRequestDto bookingRequestDto) {
        // This should create a new booking request. Look at createChef in ChefService for an example.
        return new Booking();
    }

    public List<Booking> viewBookingsUser(UUID userId) {
        if (!bookingRepository.findByUserId(userId).isPresent()) {
            return new ArrayList<>();
        }
        return bookingRepository.findByUserId(userId).get();
    }

    public List<Booking> viewBookingsChef(UUID chefId) {
        // This should list all bookings that belong to the chef chefId
        return new ArrayList<Booking>();
    }

    public Booking viewBooking(UUID bookingId) {
        // This should get a specific booking based on the bookingId. See getChefProfile in ChefService for an example.
        return new Booking();
    }

    public void changeStatus(ChangeStatusDto changeStatusDto) {
        if (!bookingRepository.findByBookingId(changeStatusDto.getBookingId()).isPresent()) {
            throw new InvalidBookingException(changeStatusDto.getBookingId().toString());
        }

        Booking booking = bookingRepository.findByBookingId(changeStatusDto.getBookingId()).get();
        booking.setStatus(changeStatusDto.getStatus());
        bookingRepository.save(booking);
    }

    public void deleteBooking(UUID bookingId) {
        if (!bookingRepository.findByBookingId(bookingId).isPresent()) {
            throw new InvalidBookingException(bookingId.toString());
        }

        bookingRepository.deleteByBookingId(bookingId);
    }
}
