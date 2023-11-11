package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingResquest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IBookingService {
	Booking createBooking(String username, BookingResquest req);
	
	BaseResponse<Booking> Confirm(int id);
	
	BaseResponse<Booking> Cancel(BookingCancelRequest req);
}
