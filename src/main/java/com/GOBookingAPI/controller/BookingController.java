package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.services.IBookingService;

@RestController
@RequestMapping("/bookings")
public class BookingController {
	
	@Autowired
	private IBookingService bookingService;

	@Autowired
	private IUserService userService;

	@PostMapping("/create")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest)
	{
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.print(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		User user = userService.getByEmail(email);
//		User user = (User) authentication.getPrincipal();
//		System.out.println(user.getUsername());
		if(user != null) {
			return ResponseEntity.ok(bookingService.createBooking(user.getEmail(), bookingRequest));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
