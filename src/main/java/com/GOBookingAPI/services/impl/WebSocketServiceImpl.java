package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.payload.response.BookingStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.WebSocketBookingTitle;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.payload.request.BookingWebSocketRequest;
import com.GOBookingAPI.payload.response.LocationCustomerResponse;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerLocation;

@Service
public class WebSocketServiceImpl implements IWebSocketService {

	private final SimpMessagingTemplate messagingTemplate;
    private final NotificationServiceImpl notificationService;

    @Autowired
    private UserRepository userRepository;
	@Autowired
	public ManagerLocation managerLocation;
    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate, NotificationServiceImpl notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Override
    public void ListenLocationDriver(LocationWebSocketRequest websocket) {	
    	
    	 LocationDriver loca = new LocationDriver();
		 loca.setIddriver(websocket.getDriverId());
		 loca.setLocation(websocket.getLocation());
		 loca.setStatus(WebSocketBookingTitle.FREE.toString());
		 if(!managerLocation.checkAddOrUpdate(websocket.getDriverId(),WebSocketBookingTitle.FREE.toString())) {
    		 managerLocation.addData(loca);
		 }else {
			 managerLocation.updateData(loca);
		 }		
			messagingTemplate.convertAndSend("/all/booking", websocket);
    }

	@Override
	public void sendMessagePrivate(CreateMessageRequest message) {
		messagingTemplate.convertAndSendToUser(String.valueOf(message.getId_receiver()), "/specific", message);
	}

	@Override
	public void notifyBookingStatus(int userId, BookingStatusResponse resp) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/customer-status", resp);
    }

	@Override
	public void notifyToDriver(int driverId ,Booking booking) {
		System.out.println("driver" + String.valueOf(driverId));
		messagingTemplate.convertAndSendToUser(String.valueOf(driverId), "/driver", booking);
	}

	@Override
	public void notifyToCustomer(int customerId, Driver driver) {
		  messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer-infodriver", driver);
	}

}
