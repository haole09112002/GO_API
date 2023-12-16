package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.payload.response.BookingStatusResponse;

import com.GOBookingAPI.payload.response.MessagePacketResponse;
import com.GOBookingAPI.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Driver;

import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.repositories.DriverRepository;
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
    private ManagerBooking managerBooking;

    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    /*
        @author:
        @description: receive packet location from driver, update and send to customer
    */
    @Override
    public void ListenLocationDriver(LocationWebSocketRequest websocket) {
        System.out.println("==> Receive packet location driver: " + websocket.toString());
        // mo app ban socket location va status len server
        Driver driver = driverRepository.findById(websocket.getDriverId()).orElseThrow(() -> new NotFoundException("Khong tim thay Driver"));

        LocationDriver location = new LocationDriver();
        location.setDriverId(websocket.getDriverId());
        location.setLocation(websocket.getLocation());
        location.setVehicleType(driver.getFirstVehicleType().getName());

        managerLocation.addOrUpdateLocation(location, driver.getStatus());

        int customerId = managerBooking.getCustomerIdByDriverId(driver.getId());


        System.out.println("====> managerBooking.getCustomerIdByDriverId(driver.getId()) == null");
        if (customerId != -1) {
            System.out.println("DriverId: + " + driver.getId() + ", Vị trí " + location.getLocation());
            messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer_driver_location", websocket);
        }else {
            System.out.println("====> managerBooking.getCustomerIdByDriverId(driver.getId()) == null");
        }

    }

    @Override
    public void sendMessagePrivate(Message message) {
        MessagePacketResponse response = new MessagePacketResponse();
        response.setCreateAt(message.getCreateAt().getTime());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setContent(message.getContent());
        response.setConversationId(message.getConversation().getId());

        System.out.println("==> sendMessagePrivate : " + response.toString());
        messagingTemplate.convertAndSendToUser(String.valueOf(response.getSenderId()), "/message_receive", response);
        messagingTemplate.convertAndSendToUser(String.valueOf(response.getReceiverId()), "/message_receive", response);
    }

    @Override
    public void notifyBookingStatusToCustomer(int userId, BookingStatusResponse resp) {
        System.out.println("==> notifyBookingStatusTo user: " + userId + ", " + resp.toString());
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/booking_status", resp);
    }

    @Override
    public void notifyBookingToDriver(int driverId, int bookingId) {
        System.out.println("==> notifyBookingToDriver: " + bookingId + ", " + driverId);
        JSONObject json = new JSONObject();
        json.put("bookingId", bookingId);
        messagingTemplate.convertAndSendToUser(String.valueOf(driverId), "/driver_booking", json);
    }

    @Override
    public void notifyDriverToCustomer(int customerId, int driverId) {
        System.out.println("==> notifyDriverToCustomer: " + customerId + ", " + driverId);
        JSONObject json = new JSONObject();
        json.put("driverId", driverId);
        messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer_driver_info", json);
    }

    @Override
    public void notifytoDriver(int driverId, String title) {
        JSONObject json = new JSONObject();
        json.put("title", title);
        System.out.println("==> send request location to driver " + driverId);
        messagingTemplate.convertAndSendToUser(String.valueOf(driverId), "/driver_notify", json);
    }
}
