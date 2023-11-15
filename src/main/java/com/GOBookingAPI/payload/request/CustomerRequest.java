package com.GOBookingAPI.payload.request;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

	private MultipartFile avatar;
	
	private String phoneNumber;
	
	private String dateOfBirth;
	
	private String fullName;
	
	private Boolean gender;
	
	
}
