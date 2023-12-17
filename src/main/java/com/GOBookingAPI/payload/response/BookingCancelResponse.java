package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.ReasonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BookingCancelResponse{
    private int id;

    private ReasonType reasonType;

    private String content;

    private BookingStatus status;
}
