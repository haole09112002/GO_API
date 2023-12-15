package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "Conversation")
public class Conversation implements Serializable{
	
	@Id
	private int id ;
	@Column
	private Date createAt;
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	
	@Column
	private Date lastmessageTime;

	@OneToMany(mappedBy = "conversation")
	private List<Message> messages = new ArrayList<>();
	
	@OneToOne
	@JoinColumn(name = "booking_id" , referencedColumnName = "id")
	private Booking booking;
}
