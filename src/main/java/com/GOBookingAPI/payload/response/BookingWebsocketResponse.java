package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingWebsocketResponse {
    private int bookingId;
}
