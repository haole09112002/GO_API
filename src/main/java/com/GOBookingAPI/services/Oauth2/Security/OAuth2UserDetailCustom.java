package com.GOBookingAPI.services.Oauth2.Security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2UserDetailCustom implements OAuth2User , UserDetails{

	private Long id;
	
	private String username;
	
	private String password;
	
	private List<GrantedAuthority> authorities;
	
	private Map<String, Object> attributes;
	
	
	private boolean isEnabled;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;
    
	public OAuth2UserDetailCustom(Long id , String username ,String password ,List<GrantedAuthority> authorities) {
		this.id = id ;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}
	

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return String.valueOf(id);
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return isEnabled;
	}

	
}
