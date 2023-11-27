package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.payload.response.BookingWebSocketResponse;
import com.GOBookingAPI.payload.response.LocationCustomerResponse;

public interface IWebSocketService {
	void ListenLocationDriver( LocationWebSocketRequest location);
	void sendMessagePrivate(CreateMessageRequest message);
	void notifyBookingStatusToCustomer(int userId, BookingStatusResponse resp);
	void notifyBookingToDriver(int driverId,BookingWebSocketResponse booking);
	void notifyDriverToCustomer(int customerId, Driver driver);
	void updateBookStatus(int bookingId , BookingStatus status);
	void notifytoDriver(int driverId , String title);
}
