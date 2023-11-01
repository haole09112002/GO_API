package com.GOBookingAPI.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.Message;
@RestController
public class MessageController {

	@MessageMapping("/message.send")
	@SendTo("topic/public")
	public Message sendMessage(@Payload Message message) {
		return message;
	}
	
	@MessageMapping("/message.addUser")
    @SendTo("/topic/public")
	public Message addPeople(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("senderId", message.getSenderId());
		return message;
	}
}
