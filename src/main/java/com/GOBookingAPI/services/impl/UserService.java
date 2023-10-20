package com.GOBookingAPI.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.IUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService implements IUserService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User loadUserbyEmail(String email) {
		try {

			User user = userRepository.findByEmail(email);
			return user;
		}catch(Exception e) {
			log.info("Error in UserService");
			return null;
		}
	}

}
