package com.GOBookingAPI.services;

import java.util.Map;

import com.GOBookingAPI.payload.request.PaymentRequest;

public interface IPaymentService {

    void handlePaymentTransaction(Map<String, String>  paymentRequest);

}
