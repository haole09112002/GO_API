package com.GOBookingAPI.services;


import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.LoginResponse;
import com.google.common.base.Optional;

public interface IUserService {
	BaseResponse<LoginResponse> loadUserbyEmail(String email);
	
	String registerCustomer(CustomerRequest customerRequest);
	
	String registerDriver(DriverRequest driverRequest);
	
	Optional<User> findByEmail(String email);
}
