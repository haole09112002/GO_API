package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateMessageRequest {
	protected int senderId ;
	
	protected int receiverId;
	
	protected String content;
	
	protected int conversationId ;
}
