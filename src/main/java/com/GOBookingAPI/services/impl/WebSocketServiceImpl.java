package com.GOBookingAPI.services.impl;

import com.GOBookingAPI.payload.response.BookingStatusResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.repositories.DriverRepository;;
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
        // mo app ban socket location va status len server
        Driver driver = driverRepository.findById(websocket.getDriverId()).orElseThrow(() -> new NotFoundException("Khong tim thay Driver"));

        LocationDriver location = new LocationDriver();
        location.setDriverId(websocket.getDriverId());
        location.setLocation(websocket.getLocation());
        location.setVehicleType(driver.getFirstVehicleType().getName());

        managerLocation.addOrUpdateLocation(location, driver.getStatus());

        int customerId = managerBooking.getCustomerIdByDriverId(driver.getId());
        if(customerId != -1){
            JSONObject json = new JSONObject();
            json.put("driverId", location.getDriverId());
            json.put("location", location.getLocation());
            System.out.println("Vị trí " + location.getLocation());
            messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer_driver_location", json);
        }

//        if (managerLocation.checkAddOrUpdate(websocket.getDriverId())) {
////    		 loca.setStatus(driver.getStatus().toString());
//            managerLocation.addData(loca);
//        } else {
////			 if(managerLocation.checkStatus(loca.getDriverId())) {
//////				 loca.setStatus(WebSocketBookingTitle.FREE.toString());
////			 }else {
//////				 loca.setStatus(WebSocketBookingTitle.BUSY.toString());
////			 }
//            managerLocation.updateData(loca);
////            int customerId = managerBooking.CheckBooking(loca.getDriverId());
//            if (customerId != 0) {
//                JSONObject json = new JSONObject();
//                json.put("driverId", loca.getDriverId());
//                json.put("location", loca.getLocation());
//                System.out.println("Vị trí " + loca.getLocation());
//                messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer_driver_location", json);
////				 messagingTemplate.convertAndSendToUser(String.valueOf(loca.getDriverId()), "/customer_driver_location", json);
//            }
//        }
//		 managerLocation.getById(loca.getDriverId());
    }

    @Override
    public void sendMessagePrivate(CreateMessageRequest message) {
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getId_receiver()), "/message_receive", message);
        System.out.println("Tin nhan " + message.getContent());
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getId_sender()), "/message_receive", message);
    }

    @Override
    public void notifyBookingStatusToCustomer(int userId, BookingStatusResponse resp) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/booking_status", resp);
    }

    @Override
    public void notifyBookingToDriver(int driverId, int bookingId) {
        JSONObject json = new JSONObject();
        json.put("bookingId", bookingId);
        messagingTemplate.convertAndSendToUser(String.valueOf(driverId), "/driver_booking", json);
    }

    @Override
    public void notifyDriverToCustomer(int customerId, int driverId) {
        JSONObject json = new JSONObject();
        json.put("driverId", driverId);
        messagingTemplate.convertAndSendToUser(String.valueOf(customerId), "/customer_driver_info", json);
    }

//    @Override
//    public void updateBookStatus(int bookingId, BookingStatus status) {
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Khong tim thay Booking nao"));
//        booking.setStatus(status);
//        bookingRepository.save(booking);
//        notifyBookingStatusToCustomer(booking.getCustomer().getId(), new BookingStatusResponse(booking.getId(), booking.getStatus()));
//        // end booking
//        if (status.equals(BookingStatus.COMPLETE)) {
//            managerLocation.UpdateStatusDriver(booking.getDriver().getId());
//        }
//    }

    @Override
    public void notifytoDriver(int driverId, String title) {
        JSONObject json = new JSONObject();
        json.put("title", title);
        messagingTemplate.convertAndSendToUser(String.valueOf(driverId), "/driver_notify", json);
    }

}
