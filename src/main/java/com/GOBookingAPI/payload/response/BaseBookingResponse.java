package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseBookingResponse {

	private int total;
	
	private int success ;
	
	private int cancelled;
}
