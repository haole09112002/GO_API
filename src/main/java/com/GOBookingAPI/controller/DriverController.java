package com.GOBookingAPI.controller;


import com.GOBookingAPI.services.IDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/drivers")
public class DriverController {

	@Autowired
	private IDriverService driverService;

	@GetMapping
	@PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
	public ResponseEntity<?> getDriverInfo(@RequestParam (required = false, defaultValue = "-1" ) Integer id) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(driverService.getDriverInfo(email, id));
	}

	@GetMapping("/{id}/base-profile")
	public ResponseEntity<?> getDriverBaseInfo(@PathVariable Integer id) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return ResponseEntity.ok(driverService.getDriverBaseInfo(email, id));
	}
}
