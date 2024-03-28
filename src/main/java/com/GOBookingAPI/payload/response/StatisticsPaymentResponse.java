package com.GOBookingAPI.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsPaymentResponse {
	
	private long amount ;
	
	private double average;
	
	private int total ;
	
	private BasePaymentResponse details;
	
}
