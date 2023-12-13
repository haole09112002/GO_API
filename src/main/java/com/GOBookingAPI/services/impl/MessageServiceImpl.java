package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Conversation;
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
	public Message createMessage(CreateMessageRequest messageRequest) {
		Date current = new Date();
		Message message = new Message();
		message.setSenderId(messageRequest.getSenderId());
		message.setReceiverId(messageRequest.getReceiverId());
		message.setContent(messageRequest.getContent());
		message.setCreateAt(current);

		Optional<Conversation> conOptional= conservationRepository.findById(messageRequest.getConversationId());
		if(conOptional.isEmpty())
			System.out.println("==> Invalid Conversation");
		message.setConversation(conOptional.get());
		messageRepository.save(message);
		return message;
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
