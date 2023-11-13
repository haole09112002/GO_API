package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateConservationRequest {
	
	private int id_driver;
	
	private int id_customer;
	
	private int id_booking; 
}
