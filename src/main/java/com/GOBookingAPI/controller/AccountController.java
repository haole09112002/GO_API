package com.GOBookingAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.BaseException;
import com.GOBookingAPI.payload.request.AccountLogin;
import com.GOBookingAPI.payload.request.UserDTO;
import com.GOBookingAPI.payload.response.BaseResponseDTO;
//import com.GOBookingAPI.services.UserService;
import com.GOBookingAPI.security.Model.UserSecurity;
import com.GOBookingAPI.services.IUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {
	
	@Autowired
	private IUserService userService;
	
//	@PostMapping("/register")
//	public ResponseEntity<BaseResponseDTO> register(@RequestBody UserDTO userDTO){
//		return ResponseEntity.ok(userService.registerAccount(userDTO));
//	}
	
	@GetMapping("get-info")
	public ResponseEntity<?> Test(){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.print(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		User user = userService.loadUserbyEmail(email);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("check-authorization")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> check(){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.print("Im User");
		User user = userService.loadUserbyEmail(email);
		return ResponseEntity.ok(user);
	}
}
