package com.GOBookingAPI.payload.request;

import com.GOBookingAPI.entities.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResquest {
	
	private int customerId;
	
	private int driverId;
	
	private String status ;
	
	private String pickUpLocation ;
	
	private String dropOffLocation;
	
	private VehicleType vehicleType;
}
