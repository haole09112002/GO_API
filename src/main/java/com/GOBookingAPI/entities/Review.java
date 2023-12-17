package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "Review")
public class Review implements Serializable {

	@Id
	private int id;
	@Column
	private Date createAt;
	@Column
	private int rating;
	@Column
	private String content ;
	
	
	
	@OneToOne
	@JoinColumn(name = "booking_id" , referencedColumnName = "id")
	private Booking booking;
}
