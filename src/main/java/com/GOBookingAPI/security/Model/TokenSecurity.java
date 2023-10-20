package com.GOBookingAPI.security.Model;


import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.Data;

@Data
public class TokenSecurity extends AbstractAuthenticationToken{

	private String token;
	
	public TokenSecurity(String token) {
		super(null);
		this.token= token;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

}
