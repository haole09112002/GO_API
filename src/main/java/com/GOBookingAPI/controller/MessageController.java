package com.GOBookingAPI.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    	 Thread.sleep(1000);
    	notificationService.sendGlobalNotification();
    	      return message;
    }
    
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/specific", message);
    }
}
