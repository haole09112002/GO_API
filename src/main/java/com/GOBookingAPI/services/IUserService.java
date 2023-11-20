package com.GOBookingAPI.services;

import java.util.Optional;

import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.payload.response.RegisterCustomerResponse;
import org.springframework.web.multipart.MultipartFile;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.payload.request.DriverRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.LoginResponse;
import com.GOBookingAPI.payload.response.RegisterResponse;

public interface IUserService {
	BaseResponse<LoginResponse> loadUserbyEmail(String email);

	User getByEmail(String email);
	
	User registerUser(String email , String phoneNumber, MultipartFile avatar , RoleEnum role);
	
	RegisterCustomerResponse registerCustomer(MultipartFile avatar, String phoneNumber, String fullName, boolean isMale, String dateOfBirth);
	
	RegisterResponse registerDriver(DriverRequest driverRequest);
	
	Optional<User> findByEmail(String email);
	
}
