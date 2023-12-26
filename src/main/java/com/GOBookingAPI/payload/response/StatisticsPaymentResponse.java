package com.GOBookingAPI.payload.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsPaymentResponse {
	
	private long amount ;
	
	private double average;
	
	private int total ;
	
	private Map<String, BasePaymentResponse> details;
	
}
