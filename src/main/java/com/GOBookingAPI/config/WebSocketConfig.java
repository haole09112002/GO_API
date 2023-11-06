package com.GOBookingAPI.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;



@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig  implements
WebSocketMessageBrokerConfigurer 
//WebSocketConfigurer
{


	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/message").withSockJS();
		registry.addEndpoint("/message");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}

//	@Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
////        registry.addHandler(webSocketHandler(), "/websocket");
//    }
    
//    @Bean
//    public WebSocketHandle webSocketHandler() {
//        return new ServerWebSocketHandler();
//    }


}
