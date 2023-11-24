package com.GOBookingAPI.services;

import java.util.List;

import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.utils.LocationDriver;
public interface IDriverService {
	Driver findDriverBooking(String locationCustomer);
}
