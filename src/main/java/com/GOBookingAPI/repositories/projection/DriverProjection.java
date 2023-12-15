package com.GOBookingAPI.repositories.projection;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.utils.DriverStatus;

public interface DriverProjection {
	int getId();
	String getArea();
	String getFullname();
	DriverStatus getStatus();
	String getPhonenumber();
	boolean getIsnonblock();
}
