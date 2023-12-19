package com.GOBookingAPI.utils;

public enum DriverStatus {
	NOT_ACTIVATED,				// submit but not yet accept
	FREE,						// online and don't have booking
	ON_RIDE,					// in the booking
	OFF,						// offline
	REFUSED,						//
	BLOCK
}



