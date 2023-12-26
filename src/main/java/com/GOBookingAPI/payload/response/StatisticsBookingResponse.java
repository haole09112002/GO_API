package com.GOBookingAPI.payload.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsBookingResponse {

	private int total;
	
	private double average;
	
	private int success ;
	
	private int cancelled;
	
	Map<String, BaseBookingResponse> details;
}
