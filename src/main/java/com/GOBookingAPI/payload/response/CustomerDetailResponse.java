package com.GOBookingAPI.payload.response;

import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDetailResponse extends CustomersResponse {

	private Date createDate;
	private Date dateOfBirth;
	private boolean gender;
	public CustomerDetailResponse(int id, String email, String fullName, String phoneNumber, boolean inNonBlock , Date createDate 
			,Date dateOfBirth, boolean gender) {
		super(id, email, fullName, phoneNumber, inNonBlock);
		this.createDate= createDate;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
	}
}
