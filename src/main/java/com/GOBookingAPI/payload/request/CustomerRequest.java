package com.GOBookingAPI.payload.request;

import java.lang.reflect.Field;
import java.util.Date;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

	private String Role;

	private String email;
	
	private String avatar;
	
	private String phoneNumber;
	
	private Boolean isNonBlock;
	
	private Date dateOfBirth;
	
	private String fullName;
	
	private Boolean gender;
	
	
}
