package com.GOBookingAPI.config.socket;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.services.IBookingService;
import com.GOBookingAPI.services.IUserService;
import jakarta.persistence.criteria.CriteriaBuilder;
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

//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user  = userService.findByEmail(email);
//        Integer currentBookingId = bookingService.getCurrentBookingId(user);
//        attributes.put("bookingId", currentBookingId);


        // Trả về true để tiếp tục quá trình handshake
        System.out.println("==> beforeHandshake in HandshakeInterceptor");
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // Thực hiện các xử lý sau khi thực hiện handshake, nếu cần
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int id  = Integer.parseInt( authentication.getName());

        // Lấy thông tin người dùng từ cơ sở dữ liệu
        User user = userService.getById(id);
//        BookingStatusResponse resp = bookingService.getCurrentBooking(user);
        BookingStatusResponse resp = new BookingStatusResponse();

        resp.setBookingId(100);
        resp.setBookingStatus(BookingStatus.CANCELLED);
        System.out.println("==>uid " + user.getId() +" afterHandshake in HandshakeInterceptor");
        // Gửi thông điệp chứa thông tin người dùng cho client ngay sau khi handshake thành công
//        messagingTemplate.convertAndSendToUser(String.valueOf(user.getId()), "/booking_status", resp);
    }
}
