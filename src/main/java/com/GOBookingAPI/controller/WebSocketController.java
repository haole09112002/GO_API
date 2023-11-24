package com.GOBookingAPI.controller;

import com.GOBookingAPI.payload.request.BookingStatusRequest;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.services.IMessageService;
import com.GOBookingAPI.services.impl.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.services.impl.WebSocketServiceImpl;

@RestController
public class WebSocketController {

    @Autowired
    private WebSocketServiceImpl webSocketService;

    @PostMapping("/send-message")
    public void sendMessage(@RequestBody final Message message) {
        webSocketService.notifyFrontend(message.getContent());
    }

    @PostMapping("/send-private-message/{id}")
    public void sendPrivateMessage(@PathVariable final String id,
                                   @RequestBody final Message message) {
        webSocketService.notifyUser(id, message.getContent());
    }

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;         //là một thành phần của Spring Framework được sử dụng để gửi message đến các destinations trong hệ thống WebSocket.

    @Autowired
    private NotificationServiceImpl notificationService;

    @Autowired
    private IMessageService messageService;

//    @MessageMapping("/bookings/status")             // tuong tu @RequestMapping()
//    @SendToUser("/bookings/status")            // kết quả của hàm này sẽ gửi tới 1 user
//    public BookingStatusResponse send(final BookingStatusRequest request ) throws Exception {
//
//    }
}
