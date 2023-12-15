package com.GOBookingAPI.payload.response;

import java.io.Serializable;

import com.nimbusds.jose.shaded.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomersResponse {
	
	private int id;
	private String email;
	private String fullName;
	private String phoneNumber;
	private boolean inNonBlock;
}
