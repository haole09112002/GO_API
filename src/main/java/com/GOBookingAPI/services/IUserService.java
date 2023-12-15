package com.GOBookingAPI.services;

import java.util.Optional;

import com.GOBookingAPI.entities.VehicleType;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.payload.request.DriverRegisterRequest;
import com.GOBookingAPI.payload.response.*;
import org.springframework.web.multipart.MultipartFile;
import com.GOBookingAPI.entities.User;

public interface IUserService {
	BaseResponse<LoginResponse> loadUserbyEmail(String email);

	User getByEmail(String email);

	User getById(int id);

	User registerUser(String email , String phoneNumber, MultipartFile avatar , RoleEnum role);
	
	RegisterCustomerResponse registerCustomer(MultipartFile avatar, String phoneNumber, String fullName, boolean isMale, String dateOfBirth);

	RegisterResponse registerDriver(DriverRegisterRequest request);

	User findByEmail(String email);

	UserResponse getUserInfo(String email);

//	DriverInfoResponse getDriverInfo(String email, Integer driverId);

	RegisterCustomerResponse getCustomerInfo(String email);
	
	void UpdateUserIsNonBlock(boolean isnonblock, int id);
	
}
