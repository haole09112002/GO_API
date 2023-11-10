package com.GOBookingAPI.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Message;

@Service
public class WebSocketServiceImpl {

	private final SimpMessagingTemplate messagingTemplate;
    private final NotificationServiceImpl notificationService;

    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate, NotificationServiceImpl notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    public void notifyFrontend(final String message) {
    	Message message1 = new Message();
    	message1.setContent(message);
        notificationService.sendGlobalNotification();

        messagingTemplate.convertAndSend("/all/messages", message1);
    }

    public void notifyUser(final String id, final String message) {
    	Message message1 = new Message();
    	message1.setContent(message);
        notificationService.sendPrivateNotification(id);
        messagingTemplate.convertAndSendToUser(id, "/specific", message1);
    }
}
