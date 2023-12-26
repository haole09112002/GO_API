package com.GOBookingAPI.repositories.projection;

public interface StatisticsPaymentMonthProjection {

	int getMonth();
	
	long getAmount();
	
	int getTotal();
}
