package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "Booking")
public class Booking implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column
	private Date createAt; 
	@Column(nullable = false , columnDefinition = "varchar(30)")
	private String pickupLocation;
	@Column(nullable = false , columnDefinition = "varchar(30)")
	private String dropoffLocation;
	@Column(nullable = false, columnDefinition = "varchar(30)")
	private String status;
	@Column
	private Timestamp startTime;
	@Column
	private Timestamp endTime;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	
	@OneToOne(mappedBy = "booking")
	private Payment payment;
	
	@OneToOne(mappedBy = "booking")
	private Conservation conservation;
	
	@OneToOne(mappedBy = "booking")
	private Review review;
}
