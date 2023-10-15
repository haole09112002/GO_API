package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "Driver")
public class Driver implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(nullable = false , columnDefinition = "varchar(30)")
	private String fullName ;
	@Column(nullable = false)
	private Boolean gender ;
	@Column
	private Date dateOfBirth;
	@Column
	private Date startWorkDay;
	@Column(nullable = false , columnDefinition = "varchar(15)")
	private String idCard;
	@Column(nullable = false, columnDefinition = "varchar(10)")
	private String licensePlate;
	@Column(nullable = false , columnDefinition = "varchar(10)")
	private String status ;
	@Column(nullable = false, columnDefinition = "varchar(50)")
	private String activityArea;
	@Column
	private Double rating ;
	
	@OneToOne
	@JoinColumn(name = "vehicle_id" , referencedColumnName = "id")
	private VehicleType vehicle;
	
//	@OneToOne(mappedBy = "driver")
//	private User user;
	
	@OneToMany(mappedBy = "driver")
	private List<Booking> books = new ArrayList<>();
	
	@OneToMany(mappedBy = "driver")
	private List<Conservation> conservations = new ArrayList<>();
}
