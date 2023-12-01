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
import com.GOBookingAPI.services.IConservationService;
import com.GOBookingAPI.services.IDriverService;
import com.GOBookingAPI.services.IWebSocketService;
import com.GOBookingAPI.utils.DriverStatus;
import com.GOBookingAPI.utils.LocationDriver;
import com.GOBookingAPI.utils.ManagerBooking;
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
	@Autowired
	private IConservationService conservationService;
	 @Autowired
    private ManagerBooking managerBooking;
	@Override
	public Driver findDriverBooking(String locationCustomer) {
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

	private static final int WAITING_TIME_SECONDS = 30; // Thời gian chờ sau

	@Override
	public void scheduleFindDriverTask(int bookingId, String locationCustomer) {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> findAndNotifyDriver(bookingId,locationCustomer), WAITING_TIME_SECONDS, TimeUnit.SECONDS);
	}

	private void findAndNotifyDriver(int bookingId , String locationCustomer) {
		// Viết logic để tìm tài xế phù hợp dựa trên vị trí => hiện tại cứ random
		Driver driverChosen = findDriverBooking(locationCustomer);
		//// câp nhat thong tin booking => lưu db
		 Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Không tìm thấy booking id: " + bookingId));
		 booking.setDriver(driverChosen);
	     bookingRepository.save(booking);
	     managerBooking.AddData(driverChosen.getId(),booking.getCustomer().getId());
	     //cap nhat trang thai dong Map locationmanager
	     managerLocation.UpdateStatusDriver(driverChosen.getId());
		// gui thong tin tai xe ve khach
		webSocketService.notifyDriverToCustomer(booking.getCustomer().getId(), driverChosen.getId());
		// gui thong tin booking ve tai xe
		webSocketService.notifyBookingToDriver(driverChosen.getId(), booking.getId());
		
		// tao cuoc tro chuyen 
		
		conservationService.createConservation(booking.getCustomer().getId(), driverChosen.getId(), bookingId);
	}

	@Override
	public List<Driver> getDriverByStatus(DriverStatus status) {
		// TODO Auto-generated method stub
		return driverRepository.findDriverStatus(status);
	}
}
