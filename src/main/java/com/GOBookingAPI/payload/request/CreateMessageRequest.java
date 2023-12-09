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
	protected int id_sender ;
	
	protected int id_receiver;
	
	protected String content;
	
	protected int id_conversation ;
}
