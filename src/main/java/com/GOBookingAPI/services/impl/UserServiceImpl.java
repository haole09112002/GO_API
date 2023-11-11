package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Role;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.entities.VehicleType;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.payload.response.LoginResponse;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.repositories.RoleRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.repositories.VehicleRepository;
import com.GOBookingAPI.services.IUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private VehicleRepository vehicleRepository;
	
	@Autowired
	private DriverRepository driverRepository;
	
	@Override
	public BaseResponse<LoginResponse> loadUserbyEmail(String email) {
		try {
			User user = userRepository.findByEmail(email);
			if(user == null) {
				 return new BaseResponse<LoginResponse>( new LoginResponse("unregistered" , null ) ,"User not found");
			}else {
				String roleName ="";
				for(Role role : user.getRoles()) {
					roleName = role.getName();
					break;
				}
				if(!user.getIsNonBlock()) {
					return new BaseResponse<LoginResponse>(new LoginResponse("blocked" ,roleName),"User is blocked");
				}
				else {
					if(roleName.equals("DRIVER")) {
						Driver driver = driverRepository.findById(user.getId());
						if(driver.getStatus().equals("NOACTIVE")) {
							return new BaseResponse<LoginResponse>(new LoginResponse("uncheck" ,roleName),"Driver uncheck");
						}
					}
					return new BaseResponse<LoginResponse>(new LoginResponse("registered" ,roleName),"User registered");
					
				}
			}
			
		}catch(Exception e) {
			log.info("Error in UserService");
			return new BaseResponse<LoginResponse>(null, e.getMessage());
		}
	}

	@Override
	public User getByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public BaseResponse<Customer> registerCustomer(CustomerRequest customerRequest) {
		try {
			User user = new User();
			Date currentDate = new Date();
			user.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
			user.setAvatarUrl(customerRequest.getAvatar());
			user.setPhoneNumber(customerRequest.getPhoneNumber());
			user.setIsNonBlock(false);
			user.setCreateDate(currentDate);
			Set<Role> roles = new HashSet<>();
			roles.add(roleRepository.findByName("CUSTOMER"));
			user.setRoles(roles);
			userRepository.save(user);
			User usersaved = userRepository.findByEmail(user.getEmail());
			Customer newcustomer = new Customer();
			newcustomer.setId(usersaved.getId());
			newcustomer.setFullName(customerRequest.getFullName());
			newcustomer.setDateOfBirth(customerRequest.getDateOfBirth());
			newcustomer.setGender(customerRequest.getGender());
			newcustomer.setUser(usersaved);
			customerRepository.save(newcustomer);
			return new BaseResponse<Customer>( newcustomer , "Success");
		}catch(Exception e) {
			log.info("Error Register Service!");
			return new BaseResponse<Customer>( null ,e.getMessage());
		}
	}

	@Override
	public BaseResponse<Driver> registerDriver(DriverRequest driverRequest) {
		try {
			User user = new User();
			Date currentDate = new Date();
			user.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
			user.setAvatarUrl(driverRequest.getAvatar());
			user.setPhoneNumber(driverRequest.getPhoneNumber());
			user.setIsNonBlock(false);
			user.setCreateDate(currentDate);
			Set<Role> roles = new HashSet<>();
			roles.add(roleRepository.findByName("DRIVER"));
			user.setRoles(roles);
			userRepository.save(user);
			User usersaved = userRepository.findByEmail(user.getEmail());
			Driver newdriver = new Driver();
			newdriver.setId(usersaved.getId());
			newdriver.setDateOfBirth(driverRequest.getDateOfBirth());
			newdriver.setFullName(driverRequest.getFullName());
			newdriver.setGender(driverRequest.getGender());
			newdriver.setIdCard(driverRequest.getIdCard());
			newdriver.setLicensePlate(driverRequest.getLicensePlate());
			Set<VehicleType> vehicles = new HashSet<>();
			vehicles.add(vehicleRepository.findByName(driverRequest.getVehicle()));
			newdriver.setVehicles(vehicles);
			newdriver.setStatus("NOACTIVE");
			newdriver.setUser(usersaved);
			driverRepository.save(newdriver);
			return new BaseResponse<Driver>( null ,"Success");
		}catch(Exception e) {
			log.info("Error Register Service!");
			return new BaseResponse<Driver>( null ,e.getMessage());
		}
	}

}
