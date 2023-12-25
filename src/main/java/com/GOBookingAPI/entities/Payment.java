package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.Date;

import com.GOBookingAPI.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "Payment")
public class Payment implements Serializable{

	@Id
	private int id;

	@Column(nullable = false , columnDefinition = "varchar(30)")
	private String transactionId;

	@Column
	private long amount;

	@Column
	private Date timeStamp;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column(nullable = false , columnDefinition = "varchar(30)")
	private String txnRef;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@OneToOne
	@JsonIgnore
	@JoinColumn(name = "booking_id" , referencedColumnName = "id")
	private Booking booking;
}
