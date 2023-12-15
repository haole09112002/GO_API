package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.request.BookingStatusPacketRequest;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.services.*;
import com.GOBookingAPI.utils.DriverStatus;
import com.GOBookingAPI.utils.ManagerBooking;
import com.GOBookingAPI.utils.ManagerLocation;
import com.sun.security.auth.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class WebSocketController {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IWebSocketService webSocketService;

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private ManagerBooking managerBooking;

    @Autowired
    private ManagerLocation managerLocation;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IUserService userService;

    @MessageMapping("/location")
    public void sendLocation(final LocationWebSocketRequest location) throws Exception {
        webSocketService.ListenLocationDriver(location);
    }

    @MessageMapping("/message_send")
    public void sendToSpecificUser(@Payload CreateMessageRequest message) {
        System.out.println("====> receive message: " + message.toString());
        Message mess = messageService.createMessage(message);
        webSocketService.sendMessagePrivate(mess);
    }

    @MessageMapping("/booking_status")
    public void processChangeBookingStatus(@Payload BookingStatusPacketRequest req) {
        User user = userService.getById(req.getUid());
        Booking booking = bookingService.changeBookingStatusAndNotify(user.getEmail(), req.getBookingId(), req.getBookingStatus());
        if (booking != null) {
            webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
            if (booking.getDriver() != null) {
                webSocketService.notifyBookingStatusToCustomer(booking.getDriver().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));   //
                System.out.println("===> notify to driver: " + (new BookingStatusResponse(booking.getId(), booking.getStatus())).toString());
                if (booking.getStatus().equals(BookingStatus.WAITING_REFUND)) {
                    System.out.println("Booking status : " + BookingStatus.WAITING_REFUND);
                    managerBooking.deleteData(booking.getDriver().getId());
                    managerLocation.updateDriverStatus(booking.getDriver().getId(), DriverStatus.FREE);
                    paymentService.refundPayment(booking); // todo bug
//					bookingService.changeBookingStatusForAdmin(booking.getId(), BookingStatus.REFUNDED); //todo debug
                }
            }
        }
    }

    @MessageMapping("/current_booking")
    public void getCurrentBooking(Principal principal) {
        if (principal != null) {
            System.out.println("==> principal.getName(): " + principal.getName());
            User user = userService.getById(Integer.parseInt(principal.getName()));     // todo remove
            webSocketService.notifyBookingStatusToCustomer(user.getId(), new BookingStatusResponse(188574, BookingStatus.WAITING_REFUND));
        } else {
            System.out.println("==> principal.getName(): NULL");
        }
    }
}
