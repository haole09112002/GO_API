package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationWebSocketRequest {
	
	private int driverId;
	
	private String location;

	private String routeEncode;

	private double bearing;
}
