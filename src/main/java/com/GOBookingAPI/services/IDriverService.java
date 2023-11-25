package com.GOBookingAPI.services;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.utils.LocationDriver;
public interface IDriverService {
	Driver findDriverBooking(String locationCustomer);

	void scheduleFindDriverTask(Booking booking);
}
