package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.PaymentRequest;

public interface IPaymentService {

    void handlePaymentTransaction(PaymentRequest paymentRequest);

}
