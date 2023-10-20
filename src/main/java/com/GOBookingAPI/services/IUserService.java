package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.User;

public interface IUserService {
	User loadUserbyEmail(String email);
}
