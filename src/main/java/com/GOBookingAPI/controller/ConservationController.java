package com.GOBookingAPI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.CreateConservationRequest;
import com.GOBookingAPI.services.IConservationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/conservation")
public class ConservationController {
	
	
}
