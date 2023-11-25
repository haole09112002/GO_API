package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.LocationWebSocketRequest;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.payload.response.LocationCustomerResponse;

public interface IWebSocketService {
	void ListenLocationDriver( LocationWebSocketRequest location);
	void sendMessagePrivate(CreateMessageRequest message);
	void notifyBookingStatus(int userId, BookingStatusResponse resp);
	void notifyToDriver(int driverId,Booking booking);
	void notifyToCustomer(int customerId, Driver driver);
}
