package com.GOBookingAPI.config;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.GOBookingAPI.entities.Conservation;
import com.GOBookingAPI.entities.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {	
	
	private final SimpMessageSendingOperations messageTemplate;
	
	@EventListener
	public void handleWebSocketDisconnectLister(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String senderId = (String) headerAccessor.getSessionAttributes().get("senderId");
		if(senderId != null) {
			log.info("User disconnected:{}" , senderId);
			Message message = new Message();
			message.setSenderId(Integer.parseInt(senderId));
			messageTemplate.convertAndSend("/topic/public",message);
		}
	}
	

}
