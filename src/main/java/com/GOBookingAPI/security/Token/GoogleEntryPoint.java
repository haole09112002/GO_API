package com.GOBookingAPI.security.Token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

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
    	
    	int statusCode = determineStatusCode(e);
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        // Tạo đối tượng JSON chứa thông điệp lỗi
        String errorMessage =  e.getMessage();
        String json = "{\"message\":\"" + errorMessage + "\"}";

        // Ghi phản hồi JSON vào HttpServletResponse
        response.getWriter().write(json);
    }

    private int determineStatusCode(AuthenticationException e) {
        if(e.getClass().equals(LockedException.class)){
            return HttpServletResponse.SC_FORBIDDEN; // 403
        }

        if (e.getClass().equals(AccessDeniedException.class)) {
            return HttpServletResponse.SC_FORBIDDEN; // 403
        }

        if (e.getClass().equals(BadCredentialsException.class)) {
            return HttpServletResponse.SC_UNAUTHORIZED; // 401
        }

        if (e.getClass().equals(DisabledException.class)) {
            return HttpServletResponse.SC_FORBIDDEN; // 403
        }
        System.out.println("===========");
        return HttpServletResponse.SC_BAD_REQUEST; // 400 (mặc định cho các trường hợp còn lại)
    }

}
