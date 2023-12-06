package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.DriverRegisterRequest;
import com.GOBookingAPI.payload.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.GOBookingAPI.services.IUserService;

import lombok.extern.slf4j.Slf4j;

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
        UserResponse user = userService.getUserInfo(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/driver")
    public ResponseEntity<?> getDriverInfo(@RequestParam (required = false, defaultValue = "-1" ) Integer driverId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getDriverInfo(email, driverId));
    }
}
