package com.GOBookingAPI.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.Message;
@Controller
public class MessageController {

	@MessageMapping("/message")
	@SendTo("topic/public")
	public String sendMessage(@Payload Message message) {
		System.out.print("ádass");
		return "âbcds";
	}
	
	@MessageMapping("/addUser")
    @SendTo("/topic/public")
	public Message addPeople(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("senderId", message.getSenderId());
		return message;
	}
}
