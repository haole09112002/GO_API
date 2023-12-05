package com.GOBookingAPI.services;

import java.util.Map;

import com.GOBookingAPI.enums.PaymentMethod;
import com.GOBookingAPI.payload.request.PaymentRequest;

public interface IPaymentService {

    void handlePaymentTransaction(Map<String, String>  paymentRequest);

    String createPaymentLink(String email, int bookingId, PaymentMethod paymentMethod);
}
