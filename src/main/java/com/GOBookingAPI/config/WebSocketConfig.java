package com.GOBookingAPI.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.GOBookingAPI.security.Token.GoogleProvider;
import com.GOBookingAPI.services.IUserService;

import lombok.extern.slf4j.Slf4j;



@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer 
{
	@Autowired
	IUserService userService ;
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/all" , "/message_receive" , "/driver_notify" ,"/booking_status" , "/customer_driver_info" ,"/customer_driver_location", "/driver_booking");
		registry.setApplicationDestinationPrefixes("/app", "/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
		.setAllowedOrigins("http://127.0.0.1:5500" ,"http://localhost:5500" ,"http://localhost:3000","https://goapi-production-9e3a.up.railway.app")
		.setHandshakeHandler(new UserHandshakeHandler(userService))
		.withSockJS();
//		registry.addEndpoint("/ws");
	}
}
