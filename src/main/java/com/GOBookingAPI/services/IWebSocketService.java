package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.request.BookingWebSocketRequest;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.payload.response.LocationCustomerResponse;

public interface IWebSocketService {
	void notify( BookingWebSocketRequest location);
	void sendMessagePrivate(CreateMessageRequest message);
	void notifyBookingStatus(int userId, BookingStatusResponse resp);
}
