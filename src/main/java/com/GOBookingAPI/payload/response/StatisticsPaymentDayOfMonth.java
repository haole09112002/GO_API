package com.GOBookingAPI.payload.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsPaymentDayOfMonth {

	private Date day;
	
	private long totalDay;
}
