package com.chachef.controller;

import com.chachef.annotations.RequireAuth;
import com.chachef.dataobjects.AuthContext;
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

    @RequireAuth
    @PostMapping("/create")
    public ResponseEntity<Booking> bookingRequest(@RequestAttribute(value = "auth") AuthContext authContext, @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return new ResponseEntity<>(bookingService.bookingRequest(bookingRequestDto, authContext), HttpStatus.OK);
    }

    @RequireAuth
    @GetMapping("/list/user")
    public ResponseEntity<List<Booking>> viewBookingsUser(@RequestAttribute(value = "auth") AuthContext authContext) {
        return new ResponseEntity<>(bookingService.viewBookingsUser(authContext.getUserId()), HttpStatus.OK);
    }

    @RequireAuth
    @GetMapping("/list/chef/{chefId}")
    public ResponseEntity<List<Booking>> viewBookingsChef(@RequestAttribute(value = "auth") AuthContext authContext, @PathVariable UUID chefId) {
        return new ResponseEntity<>(bookingService.viewBookingsChef(chefId, authContext), HttpStatus.OK);
    }

    @RequireAuth
    @GetMapping("/view/{bookingId}")
    public ResponseEntity<Booking> viewBooking(@RequestAttribute(value = "auth") AuthContext authContext, @PathVariable UUID bookingId) {
        return new ResponseEntity<>(bookingService.viewBooking(bookingId, authContext), HttpStatus.OK);
    }

    @RequireAuth
    @PutMapping("/update-status")
    public ResponseEntity<Void> changeStatus(@RequestAttribute(value = "auth") AuthContext authContext, @Valid @RequestBody ChangeStatusDto changeStatusDto) {
        bookingService.changeStatus(changeStatusDto, authContext);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequireAuth
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@RequestAttribute(value = "auth") AuthContext authContext, @PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId, authContext);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
