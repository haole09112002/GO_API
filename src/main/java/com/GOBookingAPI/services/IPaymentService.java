package com.GOBookingAPI.services;

import java.util.Map;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.enums.PaymentMethod;
import com.GOBookingAPI.payload.request.PaymentRequest;

public interface IPaymentService {

    void handlePaymentTransaction(Map<String, String> paymentRequest);

    String createPaymentLink(String email, int bookingId, PaymentMethod paymentMethod);

    void handlePaymentIPN(String vnp_TmnCode,
                          String vnp_Amount,
                          String vnp_BankCode,
                          String vnp_BankTranNo,
                          String vnp_CardType,
                          String vnp_PayDate,
                          String vnp_OrderInfo,
                          String vnp_TransactionNo,
                          String vnp_ResponseCode,
                          String vnp_TransactionStatus,
                          String vnp_TxnRef,
                          String vnp_SecureHashType,
                          String vnp_SecureHash);

    boolean refundPayment(Booking booking);
}
