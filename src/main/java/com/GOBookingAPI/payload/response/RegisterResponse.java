package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterResponse {
	private int id;
	private String fullName;
	private String email;
	private boolean isNonBlock;
	private String phoneNumber;
	private Date dateOfBirth;
	private boolean isMale;
	private String avtUrl;
}
