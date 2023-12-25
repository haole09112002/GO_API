package com.GOBookingAPI.repositories.projection;


public interface StatisticsBookingAmountMonthProjection {
	int getMonth();
	long getAmount();
	int getCount();
}
