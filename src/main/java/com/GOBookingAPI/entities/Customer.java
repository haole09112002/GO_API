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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "Customer")
public class Customer implements Serializable{
	
	@Id
	private int id;
	@Column(nullable = false , columnDefinition = "Varchar(30)")
	private String fullName ;
	@Column(nullable = false)
	private Boolean gender ;
	@Column(nullable = false)
	private Date dateOfBirth;
//	
//	@OneToOne(mappedBy = "customer")
//	private User user;
//	
	@OneToMany(mappedBy = "customer")
	private List<FavouritePlace> favourites = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer")
	private List<Payment> payments = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer")
	private List<Booking> books = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer")
	private List<Conservation> convervations = new ArrayList<>();
}
