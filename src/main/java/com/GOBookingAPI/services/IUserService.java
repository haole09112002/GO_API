package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IUserService {
	BaseResponse<User> loadUserbyEmail(String email);
	
	BaseResponse<Customer> registerCustomer(CustomerRequest customerRequest);
	
	BaseResponse<Driver> registerDriver(DriverRequest driverRequest);
}
