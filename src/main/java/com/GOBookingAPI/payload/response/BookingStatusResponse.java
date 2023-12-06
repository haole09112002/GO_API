package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.payload.request.BookingStatusRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingStatusResponse extends BookingStatusRequest {

    private int bookingId;

    @JsonProperty("status")
    private BookingStatus bookingStatus;
}
