package com.GOBookingAPI.security.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.GOBookingAPI.entities.User;

import lombok.Data;


@Data
public class UserSecurity implements Authentication{
	
	private User user;
	
	private List<GrantedAuthority> authorities;
	
	
	public UserSecurity(User user ,List<GrantedAuthority> authorities) {
		super();
		this.user = user;
		if(authorities == null) {
          List<GrantedAuthority> authoritys = new ArrayList<GrantedAuthority>();
          authoritys.add(new SimpleGrantedAuthority("ROLE_NONE"));
          this.authorities =authoritys;
		}else {
			 this.authorities =authorities;
		}
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return user;
	}

	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	
	

}
