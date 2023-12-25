package com.GOBookingAPI.services;

import java.util.Date;
import java.util.List;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.enums.VehicleType;
import com.GOBookingAPI.payload.response.*;
import com.GOBookingAPI.utils.DriverStatus;
public interface IDriverService {
	Driver findDriverBooking(String locationCustomer, VehicleType vehicleType);

	void scheduleFindDriverTask(Booking booking, String locationCustomer);
	
	List<Driver> getDriverByStatus(DriverStatus status);

	boolean findAndNotifyDriver(Booking booking, String locationCustomer);

	DriverInfoResponse getDriverInfo(String email, Integer driverId);

	DriverBaseInfoResponse getDriverBaseInfo(String email, Integer driverId);

	DriverStatusResponse changeDriverStatus(String email, Integer driverId);

	Driver getById(int id);
	
	PagedResponse<DriverPageResponse> getDriverPageAndSort(Date from, Date to, Boolean isNonBlock, DriverStatus status, String searchField,
			String keyword, String sortType, String sortField, int size, int page);
	
	DriverActiveResponse ActiveOrRefuseDriver(String ids , String type);
	
	DriverActiveResponse blockStatus(int id,Boolean isblock);

	BookingStatisticResponse bookingStatisticByDriver(String email, Date from, Date to, Integer id);
}
