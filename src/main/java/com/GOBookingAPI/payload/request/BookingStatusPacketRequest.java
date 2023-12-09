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

public class BookingStatusPacketRequest {
    private int uid;

    private int bookingId;

    private BookingStatus bookingStatus;
}
