package com.GOBookingAPI.config;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class NonBlockInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isNonBlock = user.getIsNonBlock();
        if (!isNonBlock)
            throw new AccessDeniedException("User is block");
        return true;
    }
}
