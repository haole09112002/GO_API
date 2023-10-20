package com.GOBookingAPI.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.User;


@RestController
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/get")
	public ResponseEntity<String> index(Principal principal){
		return ResponseEntity.ok("Welcome to admin page : ");
	}
}
