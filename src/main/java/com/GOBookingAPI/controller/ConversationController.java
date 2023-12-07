package com.GOBookingAPI.controller;

import com.GOBookingAPI.entities.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.GOBookingAPI.payload.request.CreateConservationRequest;
import com.GOBookingAPI.services.IConservationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

	@GetMapping
	public Conversation getConversation(){
		return null;
	}
}
