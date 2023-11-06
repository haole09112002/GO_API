package com.GOBookingAPI.config;

import java.time.LocalTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.HtmlUtils;

import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketHandle extends TextWebSocketHandler{
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	    String request = message.getPayload();
	    log.info("Server received: {}", request);
	        
	    String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
	    log.info("Server sends: {}", response);
	    session.sendMessage(new TextMessage(response));
	}
	
	
//	@Scheduled(fixedRate = 10000)
//	void sendPeriodicMessages() throws IOException {
//	    for (WebSocketSession session : sessions) {
//	        if (session.isOpen()) {
//	            String broadcast = "server periodic message " + LocalTime.now();
//	            log.info("Server sends: {}", broadcast);
//	            session.sendMessage(new TextMessage(broadcast));
//	        }
//	    }
//	}
}
