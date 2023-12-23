package com.GOBookingAPI.services;

import java.util.Date;
import java.util.List;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.payload.response.DriverActiveResponse;
import com.GOBookingAPI.payload.response.DriverBaseInfoResponse;
import com.GOBookingAPI.payload.response.DriverInfoResponse;
import com.GOBookingAPI.payload.response.DriverStatusResponse;
import com.GOBookingAPI.payload.response.DriverPageResponse;
import com.GOBookingAPI.payload.response.PagedResponse;
import com.GOBookingAPI.utils.DriverStatus;
public interface IDriverService {
	Driver findDriverBooking(String locationCustomer, VehicleType vehicleType);

	void scheduleFindDriverTask(Booking booking, String locationCustomer);
	
	List<Driver> getDriverByStatus(DriverStatus status);

	boolean findAndNotifyDriver(Booking booking, String locationCustomer);

	DriverInfoResponse getDriverInfo(String email, Integer driverId);

	DriverBaseInfoResponse getDriverBaseInfo(String email, Integer driverId);

	DriverStatusResponse changeDriverStatus(int driverId, DriverStatus newStatus);

	Driver getById(int id);
	
	PagedResponse<DriverPageResponse> getDriverPageAndSort(Date from, Date to, Boolean isNonBlock, DriverStatus status, String searchField,
			String keyword, String sortType, String sortField, int size, int page);
	
	DriverActiveResponse ActiveOrRefuseDriver(String ids , String type);
	
	DriverActiveResponse blockStatus(String ids);
}
