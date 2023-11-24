package com.GOBookingAPI.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.BookingWebSocketRequest;
import com.GOBookingAPI.payload.response.LocationCustomerResponse;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IWebSocketService;

@Service
public class WebSocketServiceImpl implements IWebSocketService {

	private final SimpMessagingTemplate messagingTemplate;
    private final NotificationServiceImpl notificationService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate, NotificationServiceImpl notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    public void notify(BookingWebSocketRequest websocket) {	
    	
			messagingTemplate.convertAndSend("/all/booking", websocket);
//        notificationService.sendGlobalNotification();
        
    }

	@Override
	public void sendMessagePrivate(CreateMessageRequest message) {
		messagingTemplate.convertAndSendToUser(String.valueOf(message.getId_receiver()), "/specific", message);
	}

}
