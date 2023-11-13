package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.CreateMessageRequest;

public interface IMessageService {
	String createMessage(CreateMessageRequest messageRequest);
}
