package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
	@Column(columnDefinition = "Varchar(30)")
	private String fullName ;
	@Column
	private Boolean gender ;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@Column
	private Date dateOfBirth;
	
	@OneToOne
	@JoinColumn(name = "user_id" , referencedColumnName = "id")
	@JsonIgnore
	private User user;
	
	@OneToMany(mappedBy = "customer")
	@JsonIgnore
	private List<FavouritePlace> favourites = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer")
	@JsonIgnore
	private List<Payment> payments = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer")
	@JsonIgnore
	private List<Booking> books = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer")
	@JsonIgnore
	private List<Conversation> convervations = new ArrayList<>();
}
