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
	private int id_sender ;
	
	private int id_receiver;
	
	private String content;
	
	private int id_conversation ;
}
