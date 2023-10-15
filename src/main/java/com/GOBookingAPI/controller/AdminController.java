package com.GOBookingAPI.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/index")
	public ResponseEntity<String> index(Principal principal){
		return ResponseEntity.ok("Welcome to admin page : " + principal.getName());
	}
}
