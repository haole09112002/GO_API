package com.GOBookingAPI.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.GOBookingAPI.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IUserService;

import jakarta.websocket.server.PathParam;
import net.minidev.json.JSONObject;


@RestController
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/get")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> index(Principal principal){
		System.out.print(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		return ResponseEntity.ok("Welcome to admin page : ");
	}
}
