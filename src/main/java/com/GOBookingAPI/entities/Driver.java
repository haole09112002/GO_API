package com.GOBookingAPI.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.GOBookingAPI.utils.DriverStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
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
	private boolean gender ;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@Column
	private Date dateOfBirth;

	@Column
	private Date startWorkDay;

	@Column( columnDefinition = "varchar(15)")
	private String idCard;

	@Column(columnDefinition = "longtext")
	private String licensePlate;

    @Enumerated(EnumType.STRING)
	private DriverStatus status ;

	@Column( columnDefinition = "varchar(50)")
	private String activityArea;

	@Column
	private Double rating ;



	@Column
	private String drivingLicense;

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
	private List<Conversation> conservations = new ArrayList<>();

	@Column
	private String imgUrl;
}
