package com.GOBookingAPI.services.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.Role;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.entities.VehicleType;
import com.GOBookingAPI.payload.request.CustomerRequest;
import com.GOBookingAPI.payload.request.DriverRequest;
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
	public User loadUserbyEmail(String email) {
		try {

			User user = userRepository.findByEmail(email);
			return user;
		}catch(Exception e) {
			log.info("Error in UserService");
			return null;
		}
	}

	@Override
	public Customer registerCustomer(CustomerRequest customerRequest) {
		User user = new User();
	
		user.setEmail(customerRequest.getEmail());
		user.setAvatarUrl(customerRequest.getAvatar());
		user.setPhoneNumber(customerRequest.getPhoneNumber());
		user.setIsNonBlock(customerRequest.getIsNonBlock());
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setEnabled(true);
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByName(customerRequest.getRole()));
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
		return newcustomer;
	}

	@Override
	public Driver registerDriver(DriverRequest driverRequest) {
		User user = new User();
		
		user.setEmail(driverRequest.getEmail());
		user.setAvatarUrl(driverRequest.getAvatar());
		user.setPhoneNumber(driverRequest.getPhoneNumber());
		user.setIsNonBlock(driverRequest.getIsNonBlock());
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setEnabled(true);
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByName(driverRequest.getRole()));
		user.setRoles(roles);
		userRepository.save(user);
		User usersaved = userRepository.findByEmail(user.getEmail());
		Driver newdriver = new Driver();
		newdriver.setId(usersaved.getId());
		newdriver.setActivityArea(driverRequest.getActivityArea());
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
		return newdriver;
	}

}
