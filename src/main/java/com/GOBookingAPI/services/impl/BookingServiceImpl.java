package com.GOBookingAPI.services.impl;

import java.util.*;

import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.response.TravelInfoResponse;
import com.GOBookingAPI.payload.vietmap.Path;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Customer;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.BookingCancelRequest;
import com.GOBookingAPI.payload.request.BookingRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.repositories.MyUserRepository;
import com.GOBookingAPI.repositories.VehicleRepository;
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
	
	@Autowired
	private VehicleRepository vehicleRepository;
	
	@Autowired
	private MyUserRepository myUserRepository;

	@Autowired
	private MapServiceImpl mapService;
	@Override
	public Booking createBooking(String username, BookingRequest req) {
		try {
			User user = myUserRepository.findByEmail(username).orElseThrow(()-> new NotFoundException("Không tìm thấy khách hàng"));
			Customer customer = customerRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy Customer"));
			System.out.print(user.toString());
			Booking booking = new Booking();
			booking.setCustomer(customer);
			booking.setDriver(null);
			booking.setStatus(BookingStatus.WAITING);
			booking.setPickupLocation(req.getPickUpLocation());
			booking.setDropoffLocation(req.getDropOffLocation());
			mapService.getRoute(req.getPickUpLocation(), req.getDropOffLocation(), "motorcycle");
			Date currentDate = new Date();
			booking.setCreateAt(currentDate);
			bookingRepository.save(booking);
			return booking;
			
		}catch(Exception e) {
			log.error("error in bookingService", e);
			return null;
		}
	}

	@Override
	public TravelInfoResponse getTravelInfo(String pickUpLocation, String dropOffLocation) {
			Map<Integer, Double> amounts = new HashMap<>();
			for (VehicleType type : VehicleType.values()) {					//todo save database vehicle type
				VietMapResponse travel = mapService.getRoute(pickUpLocation, dropOffLocation, type.name());
				if(travel.getCode().equals("ERROR")){
					throw new BadRequestException("pickUpLocation or dropOffLocation is invalid");
				}
				Path path = travel.getPaths().get(0);
				double total = path.getDistance() * type.getPrice();
				amounts.put(type.getValue(), Math.floor(total + 0.5));
			}
			return new TravelInfoResponse(pickUpLocation, dropOffLocation, amounts);
	}

	@Override
	public BaseResponse<Booking> Confirm(int id) {
		try {
//			Booking booking = bookingRepository.findById(id);
//			if(booking != null) {
//				Date curentlydate = new Date();
//				booking.setStartTime(curentlydate);
//				bookingRepository.save(booking);
//				return new BaseResponse<Booking>( booking ,"Confirm Success");
//			}else {
//				return new BaseResponse<Booking>( null, "Confirm fail!");
//			}
			return null ;
		}catch(Exception e) {
			log.info("error in Booking Service");
			return null ;
		}
	}
	@Override
	public BaseResponse<Booking> Cancel(BookingCancelRequest req) {
		try {
//			Booking booking = bookingRepository.findById(req.getBookingId());
//			if(booking != null) {
//			booking.setReasonType(req.getReasonType());
//			booking.setContentCancel(req.getContent());
//			bookingRepository.save(booking);
//			return new BaseResponse<Booking>(null, "Cancel Success");
//			}else {
//				return new BaseResponse<Booking>( null, "Cancel fail!");
//			}
			return null;
		}catch(Exception e) {
			log.info("error in Booking Service");
			return null ;
		}
	}
	
	
	

}
