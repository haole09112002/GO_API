package com.GOBookingAPI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.UserDTO;
import com.GOBookingAPI.payload.response.BaseResponseDTO;
import com.GOBookingAPI.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
	private final UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<BaseResponseDTO> register(@RequestBody UserDTO userDTO){
		return ResponseEntity.ok(userService.registerAccount(userDTO));
	}
}
