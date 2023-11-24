package com.GOBookingAPI.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.GOBookingAPI.enums.WebSocketBookingTitle;
@Service
public class ManagerLocation {
	private List<LocationDriver> locations ;

	public ManagerLocation() {
		locations = new ArrayList<>();
	}
	public void addData(LocationDriver location) {
		locations.add(location);
	}
	public void UpdateData(LocationDriver location) {
		for(LocationDriver locaDriver : locations) {
			if(locaDriver.getIddriver() == location.getIddriver()) {
				locaDriver.setLocation(location.getLocation());
			}
		}
	}
	public List<LocationDriver> getAll(){
		return locations;
	}
	
	public List<LocationDriver> getByStatus(){
		List<LocationDriver> locaList = new ArrayList<LocationDriver>();
		for(LocationDriver locaDriver : locations) {
			if(locaDriver.getStatus().equals(WebSocketBookingTitle.READYBOOKING)) {
				locaList.add(locaDriver);
			}
		}
		return locaList;
	}
	
	public boolean checkAddOrUpdate(int idDriver) {
		for(LocationDriver locaDriver : locations) {
			if(locaDriver.getIddriver() == idDriver) {
				return true;
			}
		}
		return false; 
	}
}
