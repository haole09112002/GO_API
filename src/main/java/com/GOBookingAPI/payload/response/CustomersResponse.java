package com.GOBookingAPI.payload.response;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Role;
import com.GOBookingAPI.entities.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.shaded.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomersResponse {
	private int id;
	private String email;
	private String phoneNumber;
	private Date createDate ;
	private Boolean isNonBlock;
	private String avatarUrl;
	private String fullName ;
	private Boolean gender ;
	private Date dateOfBirth;
}
