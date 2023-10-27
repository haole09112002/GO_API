package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.payload.request.BookingResquest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.security.Model.UserSecurity;

public interface IBookingService {
	Booking createBooking(BookingResquest  req);
	
	BaseResponse<Booking> Confirm(int id);
}
