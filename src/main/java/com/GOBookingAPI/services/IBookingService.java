package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingResquest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IBookingService {
	Booking createBooking(String username, BookingResquest req);
	
	BaseResponse<?> Confirm(int id);
	
	BaseResponse<?> Cancel(BookingCancelRequest req);
}
