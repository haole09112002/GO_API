package com.GOBookingAPI.payload.response;


import com.GOBookingAPI.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
	
	private String status ;
	
	private RoleEnum role ;

	private int id;
}
