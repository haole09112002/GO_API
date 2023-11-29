package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Conservation;
import com.GOBookingAPI.entities.Message;
import com.GOBookingAPI.payload.request.CreateMessageRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.repositories.ConservationRepository;
import com.GOBookingAPI.repositories.MessageRepository;
import com.GOBookingAPI.services.IMessageService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageServiceImpl implements IMessageService {
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private ConservationRepository conservationRepository;
	@Override
	public BaseResponse<Message> createMessage(CreateMessageRequest messageRequest) {
		try {
			Date curent = new Date();
			
			Message message = new Message();
			message.setSenderId(messageRequest.getId_sender());
			message.setReceiverId(messageRequest.getId_receiver());
			message.setContent(messageRequest.getContent());
			message.setCreateAt(curent);
			
			Optional<Conservation> conOptional= conservationRepository.findById(messageRequest.getId_conservation());
			message.setConservation(conOptional.get());
			messageRepository.save(message);
			
			return new BaseResponse<Message>(null,"Success");
		}catch(Exception e) {
			log.info("Error in Service {}" , e.getMessage());
			return new BaseResponse<Message>(null,"Fail");
		}
	}
	@Override
	public List<Message> getAllMessageByConservationId(int ConservationId) {
//		List<Message> messages = messageRepository.getAllByConservationId(ConservationId);
//		isf(messages.isEmpty()) {
			return null;
//		}else {
//			return messages;
//		}
	}

}
