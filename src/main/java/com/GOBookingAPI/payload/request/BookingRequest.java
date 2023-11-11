package com.GOBookingAPI.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
	
	private String pickUpLocation ;
	
	private String dropOffLocation;
	
	private int vehicleType;
}
