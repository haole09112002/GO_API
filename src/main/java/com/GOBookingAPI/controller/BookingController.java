package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.WebSocketBookingTitle;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadCredentialsException;
import com.GOBookingAPI.payload.request.BookingStatusRequest;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.services.IUserService;
import com.GOBookingAPI.utils.AppUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.payload.request.BookingWebSocketRequest;
import com.GOBookingAPI.payload.response.BookingResponse;
import com.GOBookingAPI.services.IBookingService;
import com.GOBookingAPI.services.IConservationService;
import com.GOBookingAPI.services.IDriverService;

import java.util.Date;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private IUserService userService;

    @PostMapping
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
    public ResponseEntity<?> getTravelInfo(@RequestParam @NotBlank String pickUpLocation, @RequestParam @NotBlank String dropOffLocation) {
        return ResponseEntity.ok(bookingService.getTravelInfo(pickUpLocation, dropOffLocation));
    }

    @PutMapping("/confirm/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> confirmBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.Confirm(Integer.parseInt(bookingId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingByBookingId(@PathVariable int id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(bookingService.getBookingByBookingId(email, id));
    }

    @GetMapping
    public ResponseEntity<?> getListBooking(@RequestParam @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") String from,
                                            @RequestParam @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") String to, @RequestParam int page, @RequestParam int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(bookingService.getListBookingByUser(email, AppUtils.convertStringToDate(from), AppUtils.convertStringToDate(to), page, size));
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingStatusResponse> cancelBooking(
            @PathVariable int bookingId,
            @RequestBody BookingCancelRequest cancelRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BookingStatusResponse response = bookingService.cancelBookingForCustomer(email, bookingId, cancelRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bookingId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> changeBookingStatus(
            @PathVariable int bookingId,
            @RequestBody BookingStatusRequest cancelRequest) {      // todo validation
        BookingResponse response = bookingService.changeBookingStatusForAdmin(bookingId, cancelRequest.getBookingStatus());
        return ResponseEntity.ok(response);
    }
}
