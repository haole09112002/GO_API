package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.payload.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.services.IMessageService;
import com.GOBookingAPI.services.IWebSocketService;

@RestController
public class WebSocketController {

	@Autowired
    SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private IMessageService messageService;
	
	@Autowired
	private IWebSocketService webSocketService;
	
    @MessageMapping("/location")
    public void sendLocation(final LocationWebSocketRequest location ) throws Exception {
    	webSocketService.ListenLocationDriver(location);
    }
    
    @MessageMapping("/message_send")
    public void sendToSpecificUser(@Payload CreateMessageRequest message) {
    	Message mess =  messageService.createMessage(message);
    	webSocketService.sendMessagePrivate(mess);
    }
}
