package com.GOBookingAPI.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
	@Getter
    List<WebSocketSession> list = new ArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException, InterruptedException {
        log.info("Test message {}", message.toString());
        list.add(session);
        session.sendMessage( new TextMessage("Hello world"));
        Thread.sleep(1000);
    }
}
