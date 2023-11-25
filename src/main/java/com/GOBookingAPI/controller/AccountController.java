package com.GOBookingAPI.controller;

import java.util.List;

import com.GOBookingAPI.payload.request.DriverRegisterRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<?> registerCustomer(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(name = "isMale", required = false) boolean isMale,
            @RequestParam(name = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(name = "avatar", required = false) MultipartFile avatar,
            @RequestParam(name = "fullName") String fullName) {
        return ResponseEntity.ok(userService.registerCustomer(avatar, phoneNumber, fullName, isMale, dateOfBirth));
    }

    @PostMapping("/driver")
    public ResponseEntity<?> registerDriver(@ModelAttribute @Valid DriverRegisterRequest request
                                            ) {
//		return ResponseEntity.ok("Đang bảo trì !! heheehhee :<");
        return ResponseEntity.ok(userService.registerDriver(request));
    }

    @GetMapping("/login")
    public ResponseEntity<?> Test() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BaseResponse<LoginResponse> user = userService.loadUserbyEmail(email);
        return ResponseEntity.ok(user);
    }

}
