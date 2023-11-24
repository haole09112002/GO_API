package com.GOBookingAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.LoginResponse;
import com.GOBookingAPI.services.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {
	
	@Autowired
	private IUserService userService;
	
	@PostMapping("/customer")
	public ResponseEntity<?> registerCustomer(@ModelAttribute CustomerRequest customerRequest){
		return ResponseEntity.ok(userService.registerCustomer(customerRequest));
	}
	
	@PostMapping("/driver")
	public ResponseEntity<?> registerDriver(@ModelAttribute DriverRequest driverRequest ){
		return ResponseEntity.ok(userService.registerDriver(driverRequest ));
	}
	
	@GetMapping("/login")
	public ResponseEntity<?> Test(){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		BaseResponse<LoginResponse> user = userService.loadUserbyEmail(email);
		return ResponseEntity.ok(user);
	}
	
}
