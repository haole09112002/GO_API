package com.GOBookingAPI.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Message;

@Service
public class NotificationServiceImpl {
	 private final SimpMessagingTemplate messagingTemplate;

	    @Autowired
	    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate) {
	        this.messagingTemplate = messagingTemplate;
	    }

	    public void sendGlobalNotification() {
	        Message message1 = new Message();
	    	message1.setContent("Global Notification");
	        messagingTemplate.convertAndSend("/all/global-notifications", message1);
	    }

	    public void sendPrivateNotification(final String userId) {
	        Message message1 = new Message();
	    	message1.setContent("Private Notification");
	        messagingTemplate.convertAndSendToUser(userId,"/all/private-notifications", message1);
	    }
}
