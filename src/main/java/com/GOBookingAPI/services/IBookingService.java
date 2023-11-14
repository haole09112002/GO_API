package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.TravelInfoResponse;

public interface IBookingService {
    Booking createBooking(String username, BookingRequest req);

    BaseResponse<?> Confirm(int id);

    BaseResponse<?> Cancel(BookingCancelRequest req);

    TravelInfoResponse getTravelInfo(String pickUpLocation, String dropOffLocation);
}
