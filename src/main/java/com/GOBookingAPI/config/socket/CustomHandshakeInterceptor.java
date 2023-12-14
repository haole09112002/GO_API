package com.GOBookingAPI.config.socket;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.services.IBookingService;
import com.GOBookingAPI.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        System.out.println("==> beforeHandshake in HandshakeInterceptor");
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getByEmail(email);
        BookingStatusResponse resp = new BookingStatusResponse();
        resp.setBookingId(100);
        resp.setBookingStatus(BookingStatus.CANCELLED);
        System.out.println("==>uid " + user.getId() +" afterHandshake in HandshakeInterceptor");
        messagingTemplate.convertAndSendToUser(String.valueOf(user.getId()), "/booking_status", resp);
    }
}
// Gửi thông điệp chứa thông tin người dùng cho client ngay sau khi handshake thành công
//        webSocketService.notifytoDriver(d.getId(), "HAVEBOOKING");
//
//        json.put("title", title);
//        System.out.println("==> send request location to driver " + driverId);
//        messagingTemplate.convertAndSendToUser(String.valueOf(driverId), "/driver_notify", json);