package com.GOBookingAPI.repositories.projection;

import java.util.Date;

public interface StatisticsBookingCountAndSumProjections {
	Object getDay();
	int getCount();
	long getTotal();
}
