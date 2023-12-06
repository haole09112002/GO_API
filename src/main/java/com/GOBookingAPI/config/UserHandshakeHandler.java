package com.GOBookingAPI.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.services.IUserService;
import com.sun.security.auth.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserHandshakeHandler extends DefaultHandshakeHandler {
	
	private final IUserService userService;

	public UserHandshakeHandler(IUserService userService) {
	    this.userService = userService;
	}
	@Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
  
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.print("Email handshake" + email);
		User user  = userService.findByEmail(email);
	    log.info("User with ID '{}' opened the page", user.getId());
        return new UserPrincipal(String.valueOf(user.getId()));
//		final String randomId = UUID.randomUUID().toString();
//		log.info("User with ID '{}' opened the page", randomId);
//		return new UserPrincipal(randomId);
    }
}
