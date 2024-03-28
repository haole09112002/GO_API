package com.GOBookingAPI.payload.response;

import java.util.Date;

import com.GOBookingAPI.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class PaymentBaseInfo {
	private int id;

	private String transactionId;

	private long amount;

	private Date timeStamp;

	private PaymentMethod paymentMethod;
}
