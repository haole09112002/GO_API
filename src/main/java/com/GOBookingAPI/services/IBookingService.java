package com.GOBookingAPI.services;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.BookingResponse;
import com.GOBookingAPI.payload.response.PagedResponse;
import com.GOBookingAPI.payload.response.TravelInfoResponse;

import java.util.Date;

public interface IBookingService {
    BookingResponse createBooking(String username, BookingRequest req);

    void changeBookingStatus(String username, int bookingId, BookingStatus bookingStatus);

    BaseResponse<?> Confirm(int id);

    BaseResponse<?> Cancel(BookingCancelRequest req);

    TravelInfoResponse getTravelInfo(String pickUpLocation, String dropOffLocation);

    BookingResponse getBookingByBookingId(String email, int bookingId);

    PagedResponse<BookingResponse> getListBookingByUser(String email, Date from, Date to, int page, int size);

    void changeBookingStatus(int bookingId, BookingStatus status);
}
