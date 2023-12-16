package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.Date;
import com.GOBookingAPI.enums.BookingStatus;

import com.GOBookingAPI.enums.ReasonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	private Date createAt; 
	
	@Column(nullable = false , columnDefinition = "varchar(100)")
	private String pickUpLocation;

	@Column(nullable = false , columnDefinition = "varchar(500)")
	private String pickUpAddress;

	@Column(nullable = false , columnDefinition = "varchar(500)")
	private String dropOffAddress;

	@Column(nullable = false , columnDefinition = "varchar(100)")
	private String dropOffLocation;
	
	@Enumerated(EnumType.STRING)
	private BookingStatus status;

	@Column
	private long amount;

	@Column
	private Date startTime;
	
	@Column
	private Date endTime;

	@Enumerated(EnumType.STRING)
	private ReasonType reasonType;
	
	@Column(columnDefinition = "text") 
	private String contentCancel;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	
	@OneToOne(mappedBy = "booking")
	private Payment payment;
	
	@OneToOne(mappedBy = "booking")
	private Conversation conservation;
	
	@OneToOne(mappedBy = "booking")
	private Review review;

	@Enumerated(EnumType.STRING)
	private com.GOBookingAPI.enums.VehicleType vehicleType;
}
