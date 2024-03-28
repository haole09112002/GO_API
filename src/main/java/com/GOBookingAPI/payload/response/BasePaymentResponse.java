package com.GOBookingAPI.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BasePaymentResponse {
	
	private List<String> timeStamp;
	
	private List<Long> amount ;
	
	private List<Integer> total;
}
