package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.payload.request.BookingStatusRequest;
import com.GOBookingAPI.payload.response.*;

import java.util.Date;

public interface IBookingService {
    BookingResponse createBooking(String username, BookingRequest req);

    void changeBookingStatus(String username, int bookingId, BookingStatus bookingStatus);

    BaseResponse<?> Confirm(int id);

    BaseResponse<?> Cancel(BookingCancelRequest req);

    TravelInfoResponse getTravelInfo(String pickUpLocation, String dropOffLocation);

    BookingResponse getBookingByBookingId(String email, int bookingId);

    PagedResponse<BookingResponse> getListBookingByUser(String email, Date from, Date to, int page, int size);

    BookingResponse changeBookingStatusForAdmin(int bookingId, BookingStatus status);

    BookingStatusResponse cancelBookingForCustomer(String email, int bookingId, BookingCancelRequest req);

    boolean isDriverBelongsToCustomerBooking(User cus, int driverId);

    Booking changeBookingStatusAndNotify(String email, int bookingId, BookingStatus newStatus);
}
