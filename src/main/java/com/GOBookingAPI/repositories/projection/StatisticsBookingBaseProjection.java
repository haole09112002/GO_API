package com.GOBookingAPI.repositories.projection;

import java.util.Date;

import com.GOBookingAPI.enums.BookingStatus;

public interface StatisticsBookingBaseProjection {
	Date getDay();
	int getAmount();
	String getDropOff();
	String getPickUp();
	BookingStatus getStatus();
	String getVehicle();
}
