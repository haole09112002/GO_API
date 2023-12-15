package com.GOBookingAPI.config;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.GOBookingAPI.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class NonBlockInterceptor implements HandlerInterceptor {

	private final ObjectMapper objectMapper;

    public NonBlockInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isNonBlock = user.getIsNonBlock();

        if (!isNonBlock) {
            sendAccessDeniedResponse(response, "Bạn bị block");
            return false;
        }

        return true; // Cho phép request tiếp tục xử lý
    }

    private void sendAccessDeniedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        OutputStream out = response.getOutputStream();
        objectMapper.writeValue(out, new ErrorResponse(message));
        out.flush();
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
