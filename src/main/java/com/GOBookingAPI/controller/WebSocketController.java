package com.GOBookingAPI.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.WebSocketBookingTitle;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.payload.request.BookingWebSocketRequest;
import com.GOBookingAPI.payload.response.LocationCustomerResponse;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IMessageService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.services.impl.NotificationServiceImpl;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerLocation;

import lombok.extern.slf4j.Slf4j;
@RestController
@Slf4j
public class WebSocketController {

	@Autowired
    SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private NotificationServiceImpl notificationService;
	
	@Autowired
	private IMessageService messageService;
	
	@Autowired
	private IWebSocketService webSocketService;
	
	@Autowired
	public ManagerLocation managerLocation;
	
	 
    @MessageMapping("/application")
    @SendTo("/all/booking")
    public LocationWebSocketRequest send(final LocationWebSocketRequest location ) throws Exception {
//    	 Thread.sleep(1000);
//    	 notificationService.sendGlobalNotification();
    	 if(location.getTitle().equals(WebSocketBookingTitle.READYBOOKING.toString())) {
    		 LocationDriver loca = new LocationDriver();
    		 loca.setIddriver(location.getUserid());
    		 loca.setLocation(location.getLocation());
    		 loca.setStatus(WebSocketBookingTitle.READYBOOKING.toString());
    		 if(!managerLocation.checkAddOrUpdate(location.getUserid())) {
        		 managerLocation.addData(loca);
    		 }else {
    			 managerLocation.UpdateData(loca);
    		 }	
    	 }	
    	 return location;
    }
    
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload CreateMessageRequest message) {
    	messageService.createMessage(message);
    	webSocketService.sendMessagePrivate(message);
    }
    
    @PostMapping("/send-notify")
    public void sendNotification(@RequestBody final BookingWebSocketRequest location) {
    	webSocketService.notify(location);
    }
}
