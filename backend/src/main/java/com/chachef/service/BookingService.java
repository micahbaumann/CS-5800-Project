package com.chachef.service;

import com.chachef.dataobjects.AuthContext;
import com.chachef.dto.BookingRequestDto;
import com.chachef.dto.ChangeStatusDto;
import com.chachef.entity.Booking;
import com.chachef.entity.Chef;
import com.chachef.entity.User;
import com.chachef.repository.BookingRepository;
import com.chachef.repository.UserRepository;
import com.chachef.repository.ChefRepository;
import com.chachef.service.exceptions.InvalidBookingException;
import com.chachef.service.exceptions.UnauthorizedUser;
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

    public Booking bookingRequest(BookingRequestDto bookingRequestDto, AuthContext authContext) {

        User user = userRepository.findByUserId(authContext.getUserId())
                .orElseThrow(() -> new InvalidBookingException("Invalid user: " + authContext.getUserId()));
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
        if (bookingRepository.findByUser_UserId(userId).isEmpty()) {
            return new ArrayList<>();
        }
        return bookingRepository.findByUser_UserId(userId).get();
    }

    public List<Booking> viewBookingsChef(UUID chefId, AuthContext authContext) {
        if (chefRepository.findChefsByUser_UserId(authContext.getUserId()).isEmpty()) {
            throw new UnauthorizedUser(authContext.getUsername());
        }

        List<Chef> chefList = chefRepository.findChefsByUser_UserId(authContext.getUserId()).get();
        boolean foundChef = false;
        for (Chef chef : chefList) {
            if (chef.getChefId().equals(chefId)) {
                foundChef = true;
                break;
            }
        }

        if (!foundChef) {
            throw new UnauthorizedUser(authContext.getUsername());
        }

        if (bookingRepository.findByChef_ChefId(chefId).isEmpty()) {
            return new ArrayList<>();
        }
        return bookingRepository.findByChef_ChefId(chefId).get();
    }

    public Booking viewBooking(UUID bookingId, AuthContext authContext) {
        if(bookingRepository.findByBookingId(bookingId).isEmpty()) {
            throw new InvalidBookingException(bookingId.toString());
        }

        Booking booking = bookingRepository.findByBookingId(bookingId).get();

        boolean foundChef = false;
        if (chefRepository.findChefsByUser_UserId(authContext.getUserId()).isPresent()) {
            List<Chef> chefList = chefRepository.findChefsByUser_UserId(authContext.getUserId()).get();
            for (Chef chef : chefList) {
                if (chef.getChefId().equals(booking.getChef().getChefId())) {
                    foundChef = true;
                    break;
                }
            }
        }

        if (!foundChef && !booking.getUser().getUserId().equals(authContext.getUserId())) {
            throw new UnauthorizedUser(authContext.getUsername());
        }

        return bookingRepository.findByBookingId(bookingId).get();
    }

    public void changeStatus(ChangeStatusDto changeStatusDto, AuthContext authContext) {
        if (bookingRepository.findByBookingId(changeStatusDto.getBookingId()).isEmpty()) {
            throw new InvalidBookingException(changeStatusDto.getBookingId().toString());
        }

        Booking booking = bookingRepository.findByBookingId(changeStatusDto.getBookingId()).get();
        boolean foundChef = false;
        if (chefRepository.findChefsByUser_UserId(authContext.getUserId()).isPresent()) {
            List<Chef> chefList = chefRepository.findChefsByUser_UserId(authContext.getUserId()).get();
            for (Chef chef : chefList) {
                if (chef.getChefId().equals(booking.getChef().getChefId())) {
                    foundChef = true;
                    break;
                }
            }
        }

        if (!foundChef) {
            throw new UnauthorizedUser(authContext.getUsername());
        }

        booking.setStatus(changeStatusDto.getStatus());
        bookingRepository.save(booking);
    }

    public void deleteBooking(UUID bookingId, AuthContext authContext) {
        if (bookingRepository.findByBookingId(bookingId).isEmpty()) {
            throw new InvalidBookingException(bookingId.toString());
        }

        Booking booking = bookingRepository.findByBookingId(bookingId).get();

        boolean foundChef = false;
        if (chefRepository.findChefsByUser_UserId(authContext.getUserId()).isPresent()) {
            List<Chef> chefList = chefRepository.findChefsByUser_UserId(authContext.getUserId()).get();
            for (Chef chef : chefList) {
                if (chef.getChefId().equals(booking.getChef().getChefId())) {
                    foundChef = true;
                    break;
                }
            }
        }

        if (!foundChef && !booking.getUser().getUserId().equals(authContext.getUserId())) {
            throw new UnauthorizedUser(authContext.getUsername());
        }

        bookingRepository.deleteByBookingId(bookingId);
    }
}
