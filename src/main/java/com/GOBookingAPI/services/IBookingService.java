package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.payload.request.BookingResquest;
import com.GOBookingAPI.payload.response.BookingResponse;
import com.GOBookingAPI.security.Model.UserSecurity;

public interface IBookingService {
	Booking createBooking(BookingResquest  req);
}
