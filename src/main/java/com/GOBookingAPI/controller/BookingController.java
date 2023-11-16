package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadCredentialsException;
import com.GOBookingAPI.services.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.services.IBookingService;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private IUserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createBooking(@RequestBody @Valid BookingRequest bookingRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(bookingService.createBooking(user.getEmail(), bookingRequest));
        }
        throw new AccessDeniedException("User don't have permit to access");
    }

    @GetMapping("/travel-info")
//    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createBooking(@RequestParam @NotBlank String pickUpLocation, @RequestParam @NotBlank String dropOffLocation) {
            return ResponseEntity.ok(bookingService.getTravelInfo(pickUpLocation, dropOffLocation));
    }

    @PutMapping("/confirm/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> confirmBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.Confirm(Integer.parseInt(bookingId)));
    }

    @PutMapping("/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> CancelBooking(@RequestBody BookingCancelRequest bookingCancelRequest) {
        return ResponseEntity.ok(bookingService.Cancel(bookingCancelRequest));
    }
}
