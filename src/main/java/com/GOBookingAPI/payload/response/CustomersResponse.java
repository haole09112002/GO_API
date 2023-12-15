package com.GOBookingAPI.payload.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.shaded.gson.annotations.SerializedName;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomersResponse {
	
	private int id;
	private String email;
	private String fullName;
	private String phoneNumber;
	@JsonProperty("isNonBlock")
	private boolean inNonBlock;
}
