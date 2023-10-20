package com.GOBookingAPI.security.Token;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.security.Model.TokenSecurity;
import com.GOBookingAPI.security.Model.UserSecurity;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FirebaseProvider 
implements AuthenticationProvider
{
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			TokenSecurity token = (TokenSecurity) authentication;
			FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token.getToken(),true);
			String uid = firebaseToken.getUid();
			UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
			User user = userRepository.findByEmail(userRecord.getEmail());
			if(user == null) {
				return new UserSecurity(userRecord ,null);
				
			}else {
				return new UserSecurity(userRecord, user.getRoles().stream().map(role-> new SimpleGrantedAuthority(role.getName().toString())).collect(Collectors.toList()));
			}
			
		}catch(FirebaseAuthException e) {
			log.info("Fail in Provider " , getErrorCode(e.getAuthErrorCode()));
			return null;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.isAssignableFrom(TokenSecurity.class);
	}

	private String getErrorCode(AuthErrorCode errorCode) {
		String error;
		switch(errorCode.toString()) {
			case "EXPIRED_ID_TOKEN"  :
				error = "token expired";
				break;
			case "INVALID_ID_TOKEN" :
				error = "token invalid";
				break;
			case "REVOKED_ID_TOKEN" :
				error = "token revoked";
				break;
			case "CONFIGURATION_NOT_FOUND" :
				error = "config not found";
				break;
			default :
				error ="authentication fail";
		}
		return error;
	}
}
