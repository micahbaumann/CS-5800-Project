package com.chachef.service;

import com.chachef.dto.BookingRequestDto;
import com.chachef.dto.ChangeStatusDto;
import com.chachef.entity.Booking;
import com.chachef.repository.BookingRepository;
import com.chachef.repository.UserRepository;
import com.chachef.repository.ChefRepository;
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
    private UserRepository userRepository;
    private ChefRepository chefRepository;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ChefRepository chefRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.chefRepository = chefRepository;
    }

    public Booking bookingRequest(BookingRequestDto bookingRequestDto) {

        var user = userRepository.findByUserId(bookingRequestDto.getUserId())
                .orElseThrow(() -> new InvalidBookingException("Invalid user: " + bookingRequestDto.getUserId()));
        var chef = chefRepository.findByChefId(bookingRequestDto.getChefId())
                .orElseThrow(() -> new InvalidBookingException("Invalid chef: " + bookingRequestDto.getChefId()));

        if (bookingRequestDto.getStart() != null && bookingRequestDto.getEnd() != null
                && bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new InvalidBookingException("start must be before end");
        }

        var booking = new Booking();
        booking.setUser(user);
        booking.setChef(chef);
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setAddress(bookingRequestDto.getAddress());
        booking.setStatus("PENDING"); // adjust if you use an enum

        return bookingRepository.save(booking);
    }

    public List<Booking> viewBookingsUser(UUID userId) {
        if (!bookingRepository.findByUserId(userId).isPresent()) {
            return new ArrayList<>();
        }
        return bookingRepository.findByUserId(userId).get();
    }

    public List<Booking> viewBookingsChef(UUID chefId) {
        if (!bookingRepository.findByChefId(chefId).isPresent()) {
            return new ArrayList<>();
        }
        return bookingRepository.findByChefId(chefId).get();
    }

    public Booking viewBooking(UUID bookingId) {
        if(!bookingRepository.findByBookingId(bookingId).isPresent()) {
            throw  new InvalidBookingException(bookingId.toString());
        }
        return bookingRepository.findByBookingId(bookingId).get();
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
