package com.GOBookingAPI.controller;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.services.CustomerService;
import com.GOBookingAPI.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private IUserService userService;

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public ResponseEntity<?> getCustomer(@PathVariable(required = false) int id){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getByEmail(email);
		if(user.getFirstRole().getName().equals(RoleEnum.CUSTOMER)){
			return ResponseEntity.ok(customerService.getById(user.getId()));
		}
		return ResponseEntity.ok(customerService.getById(id));
	}

	@GetMapping("/{id}/base-info")
	public ResponseEntity<?> getBaseInfoCustomer(@PathVariable int id){
		return ResponseEntity.ok(customerService.getBaseInfoById(id));
	}
}
