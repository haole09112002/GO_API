package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BasePaymentResponse {
	private long amount ;
	
	private int total;
}
