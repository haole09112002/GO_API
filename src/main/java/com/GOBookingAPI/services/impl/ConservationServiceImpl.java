package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Conservation;
import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.payload.request.CreateConservationRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.ConservationRepository;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.services.IConservationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConservationServiceImpl implements IConservationService {

	@Autowired
	private ConservationRepository conservationRepository;
	
	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Override
	public BaseResponse<?> createConservation(CreateConservationRequest conservationRequest) {
		try {
			Optional<Driver> driverOptional = driverRepository.findById(conservationRequest.getId_driver());
			if(!driverOptional.isPresent()) {
				return null;
			}
			Optional<Customer> customerOptional = customerRepository.findById(conservationRequest.getId_customer());
			if(!customerOptional.isPresent()) {
				return null;
			}
			Optional<Booking> bookOptional = bookingRepository.findById(conservationRequest.getId_booking());
			if(!customerOptional.isPresent()) {
				return null;
			}
			Date curent = new Date();
			
			Conservation conservation = new Conservation();
			conservation.setDriver(driverOptional.get());
			conservation.setCustomer(customerOptional.get());
			conservation.setBooking(bookOptional.get());
			conservation.setCreateAt(curent);
			
			conservationRepository.save(conservation);
			return new BaseResponse<Conservation>(null,"Success");
		}catch(Exception e) {
			log.info("Error in Service {}" , e.getMessage());
			return new BaseResponse<Conservation>(null,"Fail");
		}
	}

}
