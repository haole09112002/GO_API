package com.GOBookingAPI.controller;

import com.GOBookingAPI.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public ResponseEntity<?> getCustomer(@PathVariable int id){
		return ResponseEntity.ok(customerService.getById(id));
	}

	@GetMapping("/{id}/base-info")
	public ResponseEntity<?> getBaseInfoCustomer(@PathVariable int id){
		return ResponseEntity.ok(customerService.getBaseInfoById(id));
	}
}
