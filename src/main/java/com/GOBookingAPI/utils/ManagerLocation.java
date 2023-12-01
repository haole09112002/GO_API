package com.GOBookingAPI.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.GOBookingAPI.enums.WebSocketBookingTitle;

@Service
public class ManagerLocation {
	private Map<Integer, LocationDriver> locationMapFree;
	private Map<Integer, LocationDriver> locationMapBusy;
	public ManagerLocation() {
		locationMapFree = new HashMap<>();
		locationMapBusy = new HashMap<>();
	}

	public void addData(LocationDriver location) {
		if(location.getStatus().equals(WebSocketBookingTitle.FREE.toString())) {
			locationMapFree.put(location.getIddriver(), location);
		}else  {
			locationMapBusy.put(location.getIddriver(), location);
		}
		
	}

	public void updateData(LocationDriver location) {
		if (locationMapFree.containsKey(location.getIddriver())) {
				locationMapFree.put(location.getIddriver(), location);
		}else  {
			locationMapBusy.put(location.getIddriver(), location);
		}
	}


	public List<LocationDriver> getByStatus(String status) {
		List<LocationDriver> locaList = new ArrayList<>();
		if(status.equals(WebSocketBookingTitle.FREE.toString())) {
			for (LocationDriver locaDriver : locationMapFree.values()) {
				if (locaDriver.getStatus().equals(status)) {
					locaList.add(locaDriver);
				}
			}
		}else {
			for (LocationDriver locaDriver : locationMapBusy.values()) {
				if (locaDriver.getStatus().equals(status)) {
					locaList.add(locaDriver);
				}
			}
		}
		return locaList;
	}

	public boolean checkAddOrUpdate(int idDriver) {
		if(locationMapFree.containsKey(idDriver) || locationMapBusy.containsKey(idDriver)) {
			return false;
		} else  {
			return true;
		}
	}
	
	public LocationDriver getById(int driverId) {
		if(locationMapFree.containsKey(driverId)) {
			return locationMapFree.get(driverId);
		}else {
			return locationMapBusy.get(driverId);
			
		}
	}
	public void UpdateStatusDriver(int driverId) {
		if(locationMapFree.containsKey(driverId)) {
			LocationDriver location = locationMapFree.get(driverId);
			location.setStatus(WebSocketBookingTitle.BUSY.toString());
			locationMapBusy.put(location.getIddriver(), location);
			locationMapFree.remove(driverId);
		}else if(locationMapBusy.containsKey(driverId)){
			LocationDriver location = locationMapBusy.get(driverId);
			location.setStatus(WebSocketBookingTitle.FREE.toString());
			locationMapFree.put(location.getIddriver(), location);
			locationMapBusy.remove(driverId);
		}
	}
	
	public void DeteleData(int driverId) {
		if(locationMapFree.containsKey(driverId)) {
			locationMapFree.remove(driverId);
		}else if(locationMapBusy.containsKey(driverId)) {
			locationMapBusy.remove(driverId);		}
	}
	
	public boolean checkStatus(int driverId) {
		if(locationMapFree.containsKey(driverId)) {
			return true;
		}else {
			return false;
		}
	}
}
