	package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.payload.response.BookingStatusResponse;

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
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerBooking;
import com.GOBookingAPI.utils.ManagerLocation;

import net.minidev.json.JSONObject;

@Service
public class WebSocketServiceImpl implements IWebSocketService {

	private final SimpMessagingTemplate messagingTemplate;
	@Autowired
	private DriverRepository driverRepository;
	@Autowired
	public ManagerLocation managerLocation;
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private ManagerBooking managerBooking;
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
		 if(managerLocation.checkAddOrUpdate(websocket.getDriverId())) {
			 Driver driver = driverRepository.findById(websocket.getDriverId()).orElseThrow(() -> new NotFoundException("Khong tim thay Driver"));
    		 loca.setStatus(driver.getStatus().toString());
			 managerLocation.addData(loca);
		 }else {
			 if(managerLocation.checkStatus(loca.getIddriver())) {
				 loca.setStatus(WebSocketBookingTitle.FREE.toString());
			 }else {
				 loca.setStatus(WebSocketBookingTitle.BUSY.toString());
			 }
			 managerLocation.updateData(loca);
			 int customerId = managerBooking.CheckBooking(loca.getIddriver());
			 if(customerId != 0) {
				 JSONObject json = new JSONObject();
				 json.put("driverId", loca.getIddriver());
				 json.put("location", loca.getLocation());
				 System.out.println("Vị trí" + loca.getLocation());
				 messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer_driver_location", json);
				 messagingTemplate.convertAndSendToUser(String.valueOf(loca.getIddriver()), "/customer_driver_location", json);
			 }
		 }
		 managerLocation.getById(loca.getIddriver());
    }

	@Override
	public void sendMessagePrivate(CreateMessageRequest message) {
		messagingTemplate.convertAndSendToUser(String.valueOf(message.getId_receiver()), "/message_receive", message);
		System.out.println("Tin nhắn" + message.getContent());
		messagingTemplate.convertAndSendToUser(String.valueOf(message.getId_sender()), "/message_receive", message);
	}

	@Override
	public void notifyBookingStatusToCustomer(int userId, BookingStatusResponse resp) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/booking_status", resp);
    }

	@Override
	public void notifyBookingToDriver(int driverId,int bookingId) {
		JSONObject json = new JSONObject() ;
		json.put("bookingId", bookingId);
		messagingTemplate.convertAndSendToUser(String.valueOf(driverId), "/driver_booking", json);
	}

	@Override
	public void notifyDriverToCustomer(int customerId, int  driverId) {
		JSONObject json = new JSONObject();
		json.put("driverId", driverId);
	    messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer_driver_info", json);
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
		messagingTemplate.convertAndSendToUser( String.valueOf(driverId),"/driver_notify", json);
	}

}
