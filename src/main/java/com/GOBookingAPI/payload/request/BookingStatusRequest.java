package com.GOBookingAPI.payload.request;

import com.GOBookingAPI.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingStatusRequest {
    protected int bookingId;
    protected BookingStatus bookingStatus;
}
