package com.GOBookingAPI.services;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.payload.response.DriverBaseInfoResponse;
import com.GOBookingAPI.payload.response.DriverInfoResponse;
import com.GOBookingAPI.payload.response.DriverStatusResponse;
import com.GOBookingAPI.utils.DriverStatus;
import com.GOBookingAPI.utils.LocationDriver;
public interface IDriverService {
	Driver findDriverBooking(String locationCustomer, VehicleType vehicleType);

	void scheduleFindDriverTask(Booking booking, String locationCustomer);
	
	List<Driver> getDriverByStatus(DriverStatus status);

	boolean findAndNotifyDriver(Booking booking, String locationCustomer);

	DriverInfoResponse getDriverInfo(String email, Integer driverId);

	DriverBaseInfoResponse getDriverBaseInfo(String email, Integer driverId);

	DriverStatusResponse changeDriverStatus(int driverId, DriverStatus newStatus);

	Driver getById(int id);
}
