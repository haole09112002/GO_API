package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.repositories.MessageRepository;
import com.GOBookingAPI.services.IMessageService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageServiceImpl implements IMessageService {
	@Autowired
	private MessageRepository messageRepository;
	@Override
	public String createMessage(CreateMessageRequest messageRequest) {
		try {
			Date curent = new Date();
			
			Message message = new Message();
			
			return "Success";
		}catch(Exception e) {
			log.info("Error in Service {}" , e.getMessage());
			return "Fail";
		}
	}

}
