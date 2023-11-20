package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.BookingResponse;
import com.GOBookingAPI.payload.response.TravelInfoResponse;

public interface IBookingService {
    BookingResponse createBooking(String username, BookingRequest req);

    void changeBookingStatus(String username, int bookingId, BookingStatus bookingStatus);

    BaseResponse<?> Confirm(int id);

    BaseResponse<?> Cancel(BookingCancelRequest req);

    TravelInfoResponse getTravelInfo(String pickUpLocation, String dropOffLocation);
}
