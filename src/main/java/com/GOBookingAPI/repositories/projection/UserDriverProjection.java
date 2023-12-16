package com.GOBookingAPI.repositories.projection;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.utils.DriverStatus;

public interface UserDriverProjection {
	Boolean getisNonBlock();
	DriverStatus getStatus();
}
