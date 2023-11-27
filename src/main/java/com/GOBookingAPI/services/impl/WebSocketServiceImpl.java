package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.payload.response.BookingWebSocketResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.WebSocketBookingTitle;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.payload.request.BookingWebSocketRequest;
import com.GOBookingAPI.payload.response.LocationCustomerResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerLocation;

import net.minidev.json.JSONObject;

@Service
public class WebSocketServiceImpl implements IWebSocketService {

	private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;
	@Autowired
	public ManagerLocation managerLocation;
	@Autowired
	private BookingRepository bookingRepository;
    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void ListenLocationDriver(LocationWebSocketRequest websocket) {	
    	 // mo app ban socket location va status len server
    	 LocationDriver loca = new LocationDriver();
		 loca.setIddriver(websocket.getDriverId());
		 loca.setLocation(websocket.getLocation());
		 loca.setStatus(websocket.getStatus());
		 if(!managerLocation.checkAddOrUpdate(websocket.getDriverId(),websocket.getStatus())) {
    		 managerLocation.addData(loca);
		 }else {
			 managerLocation.updateData(loca);
		 }		
		 if(websocket.getCustomerId() != 0) {
			 messagingTemplate.convertAndSendToUser(String.valueOf(websocket.getCustomerId()), "/customer-locaDri", loca);
		 }
//			messagingTemplate.convertAndSend("/all/booking", websocket);
    }

	@Override
	public void sendMessagePrivate(CreateMessageRequest message) {
		messagingTemplate.convertAndSendToUser(String.valueOf(message.getId_receiver()), "/specific", message);
	}

	@Override
	public void notifyBookingStatusToCustomer(int userId, BookingStatusResponse resp) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/customer-status", resp);
    }

	@Override
	public void notifyBookingToDriver(int driverId, BookingWebSocketResponse booking) {
		messagingTemplate.convertAndSendToUser(String.valueOf(driverId), "/driver", booking);
	}

	@Override
	public void notifyDriverToCustomer(int customerId, Driver driver) {
		  messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer-info", driver);
	}

	@Override
	public void updateBookStatus(int bookingId, BookingStatus status) {
		Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new NotFoundException("Khong tim thay Booking nao"));
		booking.setStatus(status);
		bookingRepository.save(booking);
		notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId() , booking.getStatus()));
		// end booking
		if(status.equals(BookingStatus.COMPLETE)) {
			 managerLocation.UpdateStatusDriver(booking.getDriver().getId());
		}	
	}

	@Override
	public void notifytoDriver(int driverId, String title) {
		
		JSONObject json = new JSONObject();
		json.put("title", title);
		messagingTemplate.convertAndSendToUser( String.valueOf(driverId),"/driver-notify", json);
	}

}
