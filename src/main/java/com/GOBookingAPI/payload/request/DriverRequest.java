package com.GOBookingAPI.payload.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequest {

	private String Role;

	private String email;
	
	private String avatar;
	
	private String phoneNumber;
	
	private Boolean isNonBlock;
	
	private Date dateOfBirth;
	
	private String fullName;
	
	private Boolean gender;
	
	private String activityArea; 
	
	private String idCard;
	
	private String licensePlate;
	
	private String vehicle;
}
