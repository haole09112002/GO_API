package com.GOBookingAPI.services;


import java.util.Optional;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.LoginResponse;

public interface IUserService {
	BaseResponse<LoginResponse> loadUserbyEmail(String email);
	
	BaseResponse<?> registerCustomer(CustomerRequest customerRequest);
	
	BaseResponse<?> registerDriver(DriverRequest driverRequest);
	
	Optional<User> findByEmail(String email);
}
