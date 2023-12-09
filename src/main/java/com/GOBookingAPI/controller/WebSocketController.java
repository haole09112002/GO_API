package com.GOBookingAPI.controller;


import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.payload.request.BookingStatusPacketRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.services.IBookingService;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.utils.DriverStatus;
import com.GOBookingAPI.utils.ManagerBooking;
import com.GOBookingAPI.utils.ManagerLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.services.IMessageService;
import com.GOBookingAPI.services.IWebSocketService;

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
		Driver driver = driverService.getById(req.getDriverId());
		Booking booking = bookingService.changeBookingStatusAndNotify(driver.getUser().getEmail(), req.getBookingId(), req.getBookingStatus());
		if(booking != null){
			webSocketService.notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
			if (booking.getDriver() != null){
				webSocketService.notifyBookingStatusToCustomer(booking.getDriver().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));   //
				managerBooking.deleteData(booking.getDriver().getId());
				managerLocation.updateDriverStatus(booking.getDriver().getId(), DriverStatus.FREE);
			}
		}
	}
}
