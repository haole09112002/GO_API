package com.GOBookingAPI.services;


import java.util.List;

import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IMessageService {
	BaseResponse<?> createMessage(CreateMessageRequest messageRequest);
	
	List<Message> getAllMessageByConservationId(int ConservationId);
}
