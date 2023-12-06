package com.GOBookingAPI.config;

import java.time.LocalTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.HtmlUtils;

import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketHandle extends TextWebSocketHandler{
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	    String request = message.getPayload();
	    log.info("Server received: {}", request);
	        
	    String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
	    log.info("Server sends: {}", response);
	    session.sendMessage(new TextMessage(response));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);

		// Gửi gói tin chào khi kết nối được thiết lập
		String greetingMessage = "Xin chào từ server!";
		session.sendMessage(new TextMessage(greetingMessage));
		System.out.println("WebSocket connection established");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// Xử lý khi kết nối đóng
		System.out.println("WebSocket connection closed");
	}
	
}
