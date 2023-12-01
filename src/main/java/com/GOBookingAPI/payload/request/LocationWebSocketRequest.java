package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationWebSocketRequest {
	
	private int driverId;
	
	private String location;
}
