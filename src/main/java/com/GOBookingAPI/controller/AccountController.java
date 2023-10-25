package com.GOBookingAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
//import com.GOBookingAPI.services.UserService;
import com.GOBookingAPI.services.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {
	
	@Autowired
	private IUserService userService;
	
	@PostMapping("/register-customer")
	public ResponseEntity<?> registerCustomer(@RequestBody CustomerRequest customerDTO){
		return ResponseEntity.ok(userService.registerCustomer(customerDTO));
	}
	
	@PostMapping("/register-driver")
	public ResponseEntity<?> registerDriver(@RequestBody DriverRequest driverRequest){
		return ResponseEntity.ok(userService.registerDriver(driverRequest));
	}
	
	@GetMapping("/get-info")
	public ResponseEntity<?> Test(){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.print(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		User user = userService.loadUserbyEmail(email);
		return ResponseEntity.ok(user);
	}
	
}
