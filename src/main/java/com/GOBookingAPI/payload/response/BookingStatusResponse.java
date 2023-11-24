package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.request.BookingStatusRequest;

public class BookingStatusResponse extends BookingStatusRequest {
    public BookingStatusResponse(int id, BookingStatus status) {
    }
}
