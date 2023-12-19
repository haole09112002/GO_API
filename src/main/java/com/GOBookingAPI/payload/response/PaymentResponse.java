package com.GOBookingAPI.payload.response;

import java.io.Serializable;
import java.util.Date;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Payment;
import com.GOBookingAPI.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse implements Serializable {

	private int id;

	private long amount;

	private long createAt;

	private PaymentMethod paymentMethod;

	public PaymentResponse(Payment payment){
		this.paymentMethod = payment.getPaymentMethod();
		this.id = payment.getId();
		this.amount = payment.getAmount();
		this.createAt = payment.getTimeStamp().getTime();
	}
}
