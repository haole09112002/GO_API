package com.GOBookingAPI.services;


import java.text.ParseException;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.LoginResponse;
import com.GOBookingAPI.payload.response.RegisterResponse;

public interface IUserService {
	BaseResponse<LoginResponse> loadUserbyEmail(String email);

	User getByEmail(String email);
	
	User registerUser(String email , String phoneNumber, MultipartFile avatar , String Namerole);
	
	RegisterResponse registerCustomer(CustomerRequest customerRequest);
	
	RegisterResponse registerDriver(DriverRequest driverRequest);
	
	Optional<User> findByEmail(String email);
	
}
