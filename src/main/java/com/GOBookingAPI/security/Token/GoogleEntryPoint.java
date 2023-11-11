package com.GOBookingAPI.security.Token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.GOBookingAPI.exceptions.BadCredentialsException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GoogleEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // Tạo đối tượng JSON chứa thông điệp lỗi
        String errorMessage = "Unauthorized: " + e.getMessage();
        String json = "{\"status\":\"UNAUTHORIZED\",\"message\":\"" + errorMessage + "\"}";

        // Ghi phản hồi JSON vào HttpServletResponse
        response.getWriter().write(json);
    }
}
