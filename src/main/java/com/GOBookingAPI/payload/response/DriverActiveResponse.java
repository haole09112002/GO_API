package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DriverActiveResponse {

	private String status;
	
	private String notify;

	public DriverActiveResponse(String status) {
		super();
		this.status = status;
	}
	
}
