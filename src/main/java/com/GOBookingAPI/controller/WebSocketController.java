package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.BookingStatusPacketRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.payload.response.UserResponse;
import com.GOBookingAPI.security.Model.UserSecurity;
import com.GOBookingAPI.services.*;
import com.GOBookingAPI.services.impl.BookingServiceImpl;
import com.GOBookingAPI.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class WebSocketController {

	@Autowired
    SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private IMessageService messageService;
	
	@Autowired
	private IWebSocketService webSocketService;

	@Autowired
	private IBookingService bookingService;

	@Autowired
	private IDriverService driverService;

	@Autowired
	private ManagerBooking managerBooking;

	@Autowired
	private ManagerLocation managerLocation;

	@Autowired
	private IPaymentService paymentService;

	@Autowired
	private IUserService userService;
	
    @MessageMapping("/location")
    public void sendLocation(final LocationWebSocketRequest location ) throws Exception {
    	webSocketService.ListenLocationDriver(location);
    }
    
    @MessageMapping("/message_send")
    public void sendToSpecificUser(@Payload CreateMessageRequest message) {
		Message mess =  messageService.createMessage(message);
    	webSocketService.sendMessagePrivate(mess);
    }

	@MessageMapping("/booking_status")
	public void processChangeBookingStatus(@Payload BookingStatusPacketRequest req) {
		User user = userService.getById(req.getUid());
		Booking booking = bookingService.changeBookingStatusAndNotify(user.getEmail(), req.getBookingId(), req.getBookingStatus());
		if(booking != null){
			webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
			if (booking.getDriver() != null){
				webSocketService.notifyBookingStatusToCustomer(booking.getDriver().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));   //
				System.out.println("===> notify to driver: " + booking.toString());
				if(booking.getStatus().equals(BookingStatus.WAITING_REFUND)){
					managerBooking.deleteData(booking.getDriver().getId());
					managerLocation.updateDriverStatus(booking.getDriver().getId(), DriverStatus.FREE);
					paymentService.refundPayment(booking); // todo bug
//					bookingService.changeBookingStatusForAdmin(booking.getId(), BookingStatus.REFUNDED); //todo debug
				}
			}
		}
	}
}
