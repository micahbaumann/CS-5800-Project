package com.chachef.service;

import com.chachef.dto.BookingRequestDto;
import com.chachef.dto.ChangeStatusDto;
import com.chachef.entity.Booking;
import com.chachef.entity.Chef;
import com.chachef.entity.User;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChefRepository chefRepository;

    public Booking bookingRequest(BookingRequestDto bookingRequestDto) {

        User user = userRepository.findByUserId(bookingRequestDto.getUserId())
                .orElseThrow(() -> new InvalidBookingException("Invalid user: " + bookingRequestDto.getUserId()));
        Chef chef = chefRepository.findByChefId(bookingRequestDto.getChefId())
                .orElseThrow(() -> new InvalidBookingException("Invalid chef: " + bookingRequestDto.getChefId()));

        if (bookingRequestDto.getStart() != null && bookingRequestDto.getEnd() != null
                && bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new InvalidBookingException("start must be before end");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setChef(chef);
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setAddress(bookingRequestDto.getAddress());

        return bookingRepository.save(booking);
    }

    public List<Booking> viewBookingsUser(UUID userId) {
        if (!bookingRepository.findByUser_UserId(userId).isPresent()) {
            return new ArrayList<>();
        }
        return bookingRepository.findByUser_UserId(userId).get();
    }

    public List<Booking> viewBookingsChef(UUID chefId) {
        if (!bookingRepository.findByChef_ChefId(chefId).isPresent()) {
            return new ArrayList<>();
        }
        return bookingRepository.findByChef_ChefId(chefId).get();
    }

    public Booking viewBooking(UUID bookingId) {
        if(!bookingRepository.findByBookingId(bookingId).isPresent()) {
            throw new InvalidBookingException(bookingId.toString());
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
