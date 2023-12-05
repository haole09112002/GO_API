package com.GOBookingAPI.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.services.IMessageService;

@RestController
@RequestMapping("/message")
public class MessageController {
	
	@Autowired
	private IMessageService messageService;
	
	@GetMapping("/{conservationId}")
	public List<Message> GetAllByConservationId(@PathVariable int conservationId) {
		return messageService.getAllMessageByConservationId(conservationId);
	}
}
