package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.payload.response.BookingStatusResponse;

public interface IWebSocketService {
    void ListenLocationDriver(LocationWebSocketRequest location);

    void sendMessagePrivate(Message message);

    void notifyBookingStatusToCustomer(int userId, BookingStatusResponse resp);

    void notifyBookingToDriver(int driverId, int bookingId);

    void notifyDriverToCustomer(int customerId, int driverId);

//    void updateBookStatus(int bookingId, BookingStatus status);

    void notifytoDriver(int driverId, String title);
}
