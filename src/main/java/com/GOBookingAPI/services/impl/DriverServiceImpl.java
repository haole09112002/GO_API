package com.GOBookingAPI.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.BookingStatus;

import com.GOBookingAPI.enums.WebSocketBookingTitle;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.response.BookingStatusResponse;
import com.GOBookingAPI.payload.response.LocationCustomerResponse;
import com.GOBookingAPI.payload.vietmap.Path;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerLocation;

@Service
public class DriverServiceImpl implements IDriverService {
	@Autowired
	private MapServiceImpl mapService;
	@Autowired
	private ManagerLocation managerLocation;
	@Autowired
	private DriverRepository driverRepository;
	@Autowired
	private IWebSocketService webSocketService;
	@Autowired
	private BookingRepository bookingRepository;
	@Override
	public Driver findDriverBooking(String locationCustomer) {
		System.out.println("2" + managerLocation.getByStatus(WebSocketBookingTitle.FREE.toString()));
		Driver driverChosen = new Driver();
		int id_driver = 0;
		double minDistance = 1000000;
			for(LocationDriver driver : managerLocation.getByStatus(WebSocketBookingTitle.FREE.toString())) {
				VietMapResponse travel = mapService.getRoute(locationCustomer, driver.getLocation(), "MOTORCYCLE");
				if(travel.getCode().equals("ERROR")){
					throw new BadRequestException("pickUpLocation or dropOffLocation is invalid");
				}
				Path path = travel.getPaths().get(0);
				if(path.getDistance() <= minDistance) {
					minDistance = path.getDistance();
					id_driver = driver.getIddriver();
					
				}
			}
		driverChosen = driverRepository.findById(id_driver).orElseThrow(() -> new NotFoundException("Khong tim thay Driver"));
		return driverChosen;
		
	}

	private static final int WAITING_TIME_SECONDS = 2; // Thời gian chờ sau

	@Override
	public void scheduleFindDriverTask(int bookingId, String locationCustomer) {
		System.out.println("Start");
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> findAndNotifyDriver(bookingId,locationCustomer), WAITING_TIME_SECONDS, TimeUnit.SECONDS);
		System.out.println("End");
	}

	private void findAndNotifyDriver(int bookingId , String locationCustomer) {
		// Viết logic để tìm tài xế phù hợp dựa trên vị trí => hiện tại cứ random
		System.out.println("1");
		Driver driverChosen = new Driver();
		int id_driver = 0;
		double minDistance = 1000000;
			for(LocationDriver driver : managerLocation.getByStatus(WebSocketBookingTitle.FREE.toString())) {
				VietMapResponse travel = mapService.getRoute(locationCustomer, driver.getLocation(), "MOTORCYCLE");
				if(travel.getCode().equals("ERROR")){
					throw new BadRequestException("pickUpLocation or dropOffLocation is invalid");
				}
				Path path = travel.getPaths().get(0);
				if(path.getDistance() <= minDistance) {
					minDistance = path.getDistance();
					id_driver = driver.getIddriver();
				}
			}
			System.out.println("2");
		driverChosen = driverRepository.findById(id_driver).orElseThrow(() -> new NotFoundException("Khong tim thay Driver"));
		//// câp nhat thong tin booking => lưu db
		 Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + bookingId));
		 booking.setStatus(BookingStatus.ON_RIDE);
	     bookingRepository.save(booking);
	     System.out.println("3");
		// gui thong tin tai xe ve khach
		webSocketService.notifyToCustomer(booking.getCustomer().getId(), driverChosen);
		System.out.println("4");
		// gui thong tin booking ve tai xe
		webSocketService.notifyToDriver(driverChosen.getId(),booking);
		System.out.println("5");
		
	}
}
