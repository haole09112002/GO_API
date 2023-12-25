package com.GOBookingAPI.payload.response;

import java.util.Date;

import com.GOBookingAPI.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsPaymentDayResponse {
	
	private Object date;
	
	private long totalAmount;
	
	private Object BaseInfo;

}
