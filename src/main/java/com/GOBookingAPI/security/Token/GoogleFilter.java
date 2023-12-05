package com.GOBookingAPI.security.Token;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.GOBookingAPI.security.Model.TokenSecurity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class GoogleFilter extends OncePerRequestFilter  {
	
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		String token = getToken(req);
		
		try {
			if(token != null) {
				System.out.println("this is Filter  ");
				SecurityContextHolder.getContext().setAuthentication(new TokenSecurity(token));
			}else {
				System.out.println("Can't get Token");
			}
		}catch(Exception e) {
			log.info("Fail in do filter" , e.getMessage());
		}
		chain.doFilter(req, res);
	}

	private String getToken(HttpServletRequest request) {
		String header1= request.getParameter("Authorization");
		String header = request.getHeader("Authorization");
		if(header != null && header.startsWith("Bearer ")) {
			return header.replace("Bearer ", "");
		}
		else if(header1 != null && header1.startsWith("Bearer ")) {
			return header1.replace("Bearer ", ""); 
		}
		return null;
	}
	
}
