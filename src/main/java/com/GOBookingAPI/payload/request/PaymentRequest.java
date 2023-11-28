package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentRequest {

    private String transactionId;

    private Double amount;

    private Date timeStamp;

    private int bookingId;

    private String email;
}
