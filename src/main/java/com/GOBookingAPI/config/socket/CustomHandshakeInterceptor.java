package com.GOBookingAPI.config.socket;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.services.IBookingService;
import com.GOBookingAPI.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.print("Email handshake :" + email);
        User user  = userService.findByEmail(email);
//        log.info("User with ID '{}' opened the page", user.getId());
        Integer currentBookingId = bookingService.getCurrentBookingId(user);
        // Thực hiện các xử lý trước khi thực hiện handshake, nếu cần
        attributes.put("bookingId", currentBookingId);
        // Gửi gói tin từ server về client
//        String message = "Hello from server!";
//        response.getHeaders().add("Custom-Header", message);

        // Trả về true để tiếp tục quá trình handshake
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // Thực hiện các xử lý sau khi thực hiện handshake, nếu cần
    }
}
