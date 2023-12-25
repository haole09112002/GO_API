package com.GOBookingAPI.payload.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsBookingDateResponse {

	private Object date;
	
	private int numberBooking ;
	
	private long totalAmount;
	
	private long totalAmountSuccess;
	private long totalAmountFail;
	
	private double avgAmount;
	
	
}
