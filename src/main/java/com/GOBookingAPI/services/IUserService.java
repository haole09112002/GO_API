package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;

public interface IUserService {
	User loadUserbyEmail(String email);
	
	Customer registerCustomer(CustomerRequest customerRequest);
	
	Driver registerDriver(DriverRequest driverRequest);
}
