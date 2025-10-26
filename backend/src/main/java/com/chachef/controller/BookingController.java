package com.chachef.controller;

import com.chachef.dto.BookingRequestDto;
import com.chachef.dto.ChangeStatusDto;
import com.chachef.entity.Booking;
import com.chachef.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<Booking> bookingRequest(@Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return new ResponseEntity<>(bookingService.bookingRequest(bookingRequestDto), HttpStatus.OK);
    }

    @GetMapping("/list/user/{userId}")
    public ResponseEntity<List<Booking>> viewBookingsUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(bookingService.viewBookingsUser(userId), HttpStatus.OK);
    }

    @GetMapping("/list/chef/{chefId}")
    public ResponseEntity<List<Booking>> viewBookingsChef(@PathVariable UUID chefId) {
        return new ResponseEntity<>(bookingService.viewBookingsChef(chefId), HttpStatus.OK);
    }

    @GetMapping("/view/{bookingId}")
    public ResponseEntity<Booking> viewBooking(@PathVariable UUID bookingId) {
        return new ResponseEntity<>(bookingService.viewBooking(bookingId), HttpStatus.OK);
    }

    @PutMapping("/update-status")
    public ResponseEntity<Void> changeStatus(@Valid @RequestBody ChangeStatusDto changeStatusDto) {
        bookingService.changeStatus(changeStatusDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
