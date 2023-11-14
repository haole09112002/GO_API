package com.GOBookingAPI.services;


import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IMessageService {
	BaseResponse<?> createMessage(CreateMessageRequest messageRequest);
}
