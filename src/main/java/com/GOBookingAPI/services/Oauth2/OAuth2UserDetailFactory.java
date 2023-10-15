package com.GOBookingAPI.services.Oauth2;

import java.util.Map;

import com.GOBookingAPI.entities.Provider;
import com.GOBookingAPI.exceptions.BaseException;

public class OAuth2UserDetailFactory {

	public static OAuth2UserDetails getAuth2UserDetails(String registrationId , Map<String, Object> attributes) {
		if(registrationId.equals(Provider.google.name())) {
			return new OAuth2GoogleUser(attributes);
		}else {
			throw new BaseException("400" , "Sorry! login with " + registrationId + " is not supported");
		}
	}
}
