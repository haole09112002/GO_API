package com.GOBookingAPI.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class ManagerLocation {

	private Map<Integer, LocationDriver> locationMapFree;
	private Map<Integer, LocationDriver> locationMapBusy;

	public ManagerLocation() {
		locationMapFree = new HashMap<>();
		locationMapBusy = new HashMap<>();
	}

//	public void addData(LocationDriver location) {
//		if(location.getStatus().equals(WebSocketBookingTitle.FREE.toString())) {
//			locationMapFree.put(location.getDriverId(), location);
//		}else  {
//			locationMapBusy.put(location.getDriverId(), location);
//		}
//
//	}

//	public void updateData(LocationDriver location) {
//		if (locationMapFree.containsKey(location.getDriverId())) {
//				locationMapFree.put(location.getDriverId(), location);
//		}else  {
//			locationMapBusy.put(location.getDriverId(), location);
//		}
//	}


//	public List<LocationDriver> getByStatus(String status) {
//		List<LocationDriver> locaList = new ArrayList<>();
//		if(status.equals(WebSocketBookingTitle.FREE.toString())) {
//			for (LocationDriver locaDriver : locationMapFree.values()) {
//				if (locaDriver.getStatus().equals(status)) {
//					locaList.add(locaDriver);
//				}
//			}
//		}else {
//			for (LocationDriver locaDriver : locationMapBusy.values()) {
//				if (locaDriver.getStatus().equals(status)) {
//					locaList.add(locaDriver);
//				}
//			}
//		}
//		return locaList;
//	}

//	public boolean checkAddOrUpdate(int idDriver) {
//		if(locationMapFree.containsKey(idDriver) || locationMapBusy.containsKey(idDriver)) {
//			return false;
//		} else  {
//			return true;
//		}
//	}
//
//	public LocationDriver getById(int driverId) {
//		if(locationMapFree.containsKey(driverId)) {
//			return locationMapFree.get(driverId);
//		}else {
//			return locationMapBusy.get(driverId);
//
//		}
//	}
//	public void UpdateStatusDriver(int driverId) {
//		if(locationMapFree.containsKey(driverId)) {
//			LocationDriver location = locationMapFree.get(driverId);
//			locationMapBusy.put(location.getDriverId(), location);
//			locationMapFree.remove(driverId);
//		}else if(locationMapBusy.containsKey(driverId)){
//			LocationDriver location = locationMapBusy.get(driverId);
//			locationMapFree.put(location.getDriverId(), location);
//			locationMapBusy.remove(driverId);
//		}
//	}
	
	public void deleteData(int driverId) {
		if(locationMapFree.containsKey(driverId)) {
			locationMapFree.remove(driverId);
		}else if(locationMapBusy.containsKey(driverId)) {
			locationMapBusy.remove(driverId);		}
	}
	
//	public boolean checkStatus(int driverId) {
//		if(locationMapFree.containsKey(driverId)) {
//			return true;
//		}else {
//			return false;
//		}
//	}

	/*
	    @author: HaoLV
	    @description: add or update location of driver with driver status
	*/
	public void addOrUpdateLocation(LocationDriver locationDriver, DriverStatus driverStatus){
		if(driverStatus.equals(DriverStatus.FREE)){
			System.out.println("==> addOrUpdateLocation, DriverStatus.FREE");
			this.locationMapBusy.remove(locationDriver.getDriverId());
			this.locationMapFree.put(locationDriver.getDriverId(), locationDriver);
		}

		if(driverStatus.equals(DriverStatus.ON_RIDE)){
			System.out.println("==> addOrUpdateLocation, DriverStatus.ON_RIDE");
			this.locationMapFree.remove(locationDriver.getDriverId());
			this.locationMapBusy.put(locationDriver.getDriverId(), locationDriver);
		}
	}

	public void updateDriverStatus(int driverId, DriverStatus driverStatus){
		if(locationMapFree.containsKey(driverId) && driverStatus.equals(DriverStatus.ON_RIDE)) {
			System.out.println("==> updateDriverStatus, DriverStatus.ON_RIDE");
			LocationDriver location = locationMapFree.get(driverId);
			locationMapFree.remove(driverId);
			locationMapBusy.put(location.getDriverId(), location);
		}

		if(locationMapBusy.containsKey(driverId) && driverStatus.equals(DriverStatus.FREE)){
			System.out.println("==> updateDriverStatus, DriverStatus.FREE");
			LocationDriver location = locationMapBusy.get(driverId);
			locationMapBusy.remove(driverId);
			locationMapFree.put(location.getDriverId(), location);
		}
	}
}
