package com.GOBookingAPI.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsPaymentBaseResponse {

	private int totalAmount;
	
	private int number;
	
	private double avg;
	
	PagedResponse<StatisticsPaymentDayResponse> page;
	
	
}
