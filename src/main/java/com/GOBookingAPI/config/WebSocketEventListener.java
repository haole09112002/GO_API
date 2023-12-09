package com.GOBookingAPI.config;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.GOBookingAPI.entities.Conversation;
import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.utils.ManagerLocation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {	
	
	private final SimpMessageSendingOperations messageTemplate;
	
	@Autowired
	private ManagerLocation managerLocation;
	
	@EventListener
	public void handleWebSocketDisconnectLister(SessionDisconnectEvent event) {
		String userId =  event.getUser().getName(); 
		log.info("User disconnected: {}" , userId  );
		managerLocation.deleteData(Integer.parseInt(userId));
	}
	

}
