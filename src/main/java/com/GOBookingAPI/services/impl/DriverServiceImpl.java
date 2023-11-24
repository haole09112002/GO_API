package com.GOBookingAPI.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.vietmap.Path;
import com.GOBookingAPI.payload.vietmap.VietMapResponse;
import com.GOBookingAPI.repositories.DriverRepository;
import com.GOBookingAPI.services.IDriverService;
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
	@Override
	public Driver findDriverBooking(String locationCustomer) {
		System.out.println("2" + managerLocation.getAll());
		Driver driverChosen = new Driver();
		int id_driver = 0;
		double minDistance = 1000000;
			for(LocationDriver driver : managerLocation.getAll()) {
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
}
