package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.UserDTO;
import com.GOBookingAPI.payload.response.BaseResponseDTO;

public interface UserService {
	BaseResponseDTO registerAccount(UserDTO userDTO);
}
	