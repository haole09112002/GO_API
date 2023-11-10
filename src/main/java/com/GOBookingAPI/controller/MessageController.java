package com.GOBookingAPI.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
<<<<<<< HEAD
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
=======
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.services.impl.NotificationServiceImpl;

import lombok.extern.slf4j.Slf4j;
@RestController
@Slf4j
public class MessageController {

	@Autowired
    SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private NotificationServiceImpl notificationService;
	
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public Message send(final Message message ) throws Exception {
//    	if(message.getContent().equals("BOOKING")) {
    	 Thread.sleep(1000);
    	notificationService.sendGlobalNotification();
    	      return message;
//    	}
//    	else {
//    		return null;
//    	}
    }
    
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/specific", message);
    }
>>>>>>> devD
}
