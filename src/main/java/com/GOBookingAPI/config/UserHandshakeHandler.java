package com.GOBookingAPI.config;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.services.IUserService;
import com.sun.security.auth.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Slf4j
public class UserHandshakeHandler extends DefaultHandshakeHandler {
	
	private final IUserService userService;

	public UserHandshakeHandler(IUserService userService) {
	    this.userService = userService;
	}
	@Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("Email handshake :" + email);
		User user  = userService.findByEmail(email);
	    log.info("User with ID '{}' opened the page", user.getId());
        return new UserPrincipal(String.valueOf(user.getId()));
    }
}
