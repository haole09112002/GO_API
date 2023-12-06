package com.GOBookingAPI.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.BookingStatusRequest;
import com.GOBookingAPI.services.IWebSocketService;

@RestController
@RequestMapping("/driver")
public class DriverController {
	
	@Autowired
	private IWebSocketService webSocketService;
	
	@PostMapping("/status-booking")
	@PreAuthorize("hasRole('DRIVER')")
	public ResponseEntity<?> start(@RequestBody BookingStatusRequest req){

//		webSocketService.updateBookStatus(req.getBookingId(), req.getBookingStatus());
		return ResponseEntity.ok("OK");
	}
	
}
