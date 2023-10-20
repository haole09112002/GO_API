package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.BookingResquest;
import com.GOBookingAPI.payload.response.BookingResponse;
import com.GOBookingAPI.security.Model.UserSecurity;

public interface IBookingService {
	BookingResponse createBooking(BookingResquest  req, UserSecurity userSecurity );
}
