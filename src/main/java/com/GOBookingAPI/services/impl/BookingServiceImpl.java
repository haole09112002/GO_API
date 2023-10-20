package com.GOBookingAPI.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.payload.request.BookingResquest;
import com.GOBookingAPI.payload.response.BookingResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.repositories.VehicleRepository;
import com.GOBookingAPI.security.Model.UserSecurity;
import com.GOBookingAPI.services.IBookingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookingServiceImpl implements IBookingService{

	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private DriverRepository driverRepository;
	
	@Override
	public BookingResponse createBooking(BookingResquest req, UserSecurity userSecurity) {
		try {
			return null ;
			
		}catch(Exception e) {
			log.info("error in bookingService");
			return null;
		}
	}

}
