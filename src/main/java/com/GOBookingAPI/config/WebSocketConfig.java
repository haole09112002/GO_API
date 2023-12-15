package com.GOBookingAPI.config;


import com.GOBookingAPI.config.socket.CustomHandshakeInterceptor;
import com.GOBookingAPI.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    IUserService userService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/all", "/message_receive", "/driver_notify", "/booking_status", "/customer_driver_info", "/customer_driver_location", "/driver_booking", "topic");
        registry.setApplicationDestinationPrefixes("/app", "/user");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(customHandshakeInterceptor())
                .setAllowedOrigins("http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:3000", "https://forlorn-bite-production.up.railway.app", "https://go-webapp.vercel.app")
                .setHandshakeHandler(new UserHandshakeHandler(userService))
                .withSockJS();
    }

    @Bean
    public CustomHandshakeInterceptor customHandshakeInterceptor() {
        return new CustomHandshakeInterceptor();
    }
}
