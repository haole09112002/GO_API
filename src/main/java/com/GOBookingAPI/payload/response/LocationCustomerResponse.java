package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationCustomerResponse {
	private String pickUpLocation;
	
	private int userid;
}
