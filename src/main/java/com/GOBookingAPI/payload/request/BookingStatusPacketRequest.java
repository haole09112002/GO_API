package com.GOBookingAPI.payload.request;

import com.GOBookingAPI.enums.BookingStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookingStatusPacketRequest {
    private int uid;

    private int bookingId;

    private BookingStatus bookingStatus;
}
