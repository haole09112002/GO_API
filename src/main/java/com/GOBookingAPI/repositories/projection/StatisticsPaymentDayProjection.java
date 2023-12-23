package com.GOBookingAPI.repositories.projection;

import java.util.Date;

public interface StatisticsPaymentDayProjection {
	Date getDate();
	long getTotal();
}
