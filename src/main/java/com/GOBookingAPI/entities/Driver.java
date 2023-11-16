package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
	private int id;
	@Column( columnDefinition = "varchar(30)")
	private String fullName ;
	@Column
	private Boolean gender ;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@Column
	private Date dateOfBirth;
	@Column
	private Date startWorkDay;
	@Column( columnDefinition = "varchar(15)")
	private String idCard;
	@Column(columnDefinition = "longtext")
	private String licensePlate;
	@Column( columnDefinition = "varchar(10)")
	private String status ;
	@Column( columnDefinition = "varchar(50)")
	private String activityArea;
	@Column
	private Double rating ;
	
//	@OneToOne
//	@JoinColumn(name = "vehicle_id" , referencedColumnName = "id")
//	private VehicleType vehicle;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "driver_vehicle", joinColumns = @JoinColumn(name = "driver_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "vehicle_id", referencedColumnName = "id"))
    private Set<VehicleType> vehicles;
	
	@OneToOne
	@JoinColumn(name = "user_id" , referencedColumnName = "id")
	@JsonIgnore
	private User user;
	
	@OneToMany(mappedBy = "driver")
	@JsonIgnore
	private List<Booking> books = new ArrayList<>();
	
	@OneToMany(mappedBy = "driver")
	@JsonIgnore
	private List<Conservation> conservations = new ArrayList<>();
}
