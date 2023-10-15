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
public class UserDTO {

	private String username;
	
	private String password;
	
	private String Role;

	private String email;
	
	private String phoneNumber;
	
	private Boolean isNonBlock;
	
	
	public String checkProperties() throws IllegalAccessException{
		for(Field f : getClass().getDeclaredFields()){
			if(f.get(this) == null) {
				String[] arr = f.toString().split("\\.");
				String t = arr[arr.length - 1] ;
				if(t.equalsIgnoreCase("username") || t.equalsIgnoreCase("password") || t.equalsIgnoreCase("role")) {
					return t;
				}
			}
		}
		return null;
	}
	
}
