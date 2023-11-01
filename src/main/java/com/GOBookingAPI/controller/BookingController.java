package com.GOBookingAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingResquest;
import com.GOBookingAPI.services.IBookingService;

@RestController
@RequestMapping("/bookings")
public class BookingController {
	
	@Autowired
	private IBookingService bookingService;

	@PostMapping("/create")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> CreateBooking(@RequestBody BookingResquest bookingResquest){
		return ResponseEntity.ok(bookingService.createBooking(bookingResquest));
	}
	
	@PutMapping("/confirm/{bookingId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> confirmBooking(@PathVariable String bookingId){
		return ResponseEntity.ok(bookingService.Confirm(Integer.parseInt(bookingId)));
	}
	
	@PutMapping("/cancel")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> CancelBooking(@RequestBody BookingCancelRequest bookingCancelRequest){
		return ResponseEntity.ok(bookingService.Cancel(bookingCancelRequest));
	}
}
