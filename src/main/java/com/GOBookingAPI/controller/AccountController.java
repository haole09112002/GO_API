package com.GOBookingAPI.controller;


import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.payload.request.DriverRegisterRequest;

import com.GOBookingAPI.services.IDriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.GOBookingAPI.services.IUserService;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

    @Autowired
    private IUserService userService;

    @PostMapping("/customer")
    public ResponseEntity<?> registerCustomer(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(name = "isMale", defaultValue = "false") boolean isMale,
            @RequestParam(name = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(name = "avatar", required = false) MultipartFile avatar,
            @RequestParam(name = "fullName") String fullName) {
        return ResponseEntity.ok(userService.registerCustomer(avatar, phoneNumber, fullName, isMale, dateOfBirth));
    }

    @PostMapping("/driver")
    public ResponseEntity<?> registerDriver(@ModelAttribute @Valid DriverRegisterRequest request) {
        return ResponseEntity.ok(userService.registerDriver(request));
    }

    @GetMapping("/login")
    public ResponseEntity<?> login() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.loadUserbyEmail(email));
    }
    
	@PutMapping("/user/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateIsNonBlock(@PathVariable("id") int id , @RequestParam("isnonblock") boolean isnonblock){
		userService.UpdateUserIsNonBlock(isnonblock, id);
		JSONObject json = new JSONObject();
		json.put("message", "Update Complete!");
		return ResponseEntity.ok(json);
	}
}
