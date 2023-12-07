package com.GOBookingAPI.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
	
	private String status ;
	
	private String role ;

	private int id;
}
