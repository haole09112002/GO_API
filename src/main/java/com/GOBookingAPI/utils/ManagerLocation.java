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
		}else if(location.getStatus().equals(WebSocketBookingTitle.BUSY.toString())) {
			locationMapBusy.put(location.getIddriver(), location);
		}
		
	}

	public void updateData(LocationDriver location) {
		if (locationMapFree.containsKey(location.getIddriver())) {
			if(location.getStatus().equals(WebSocketBookingTitle.FREE.toString())) {
				locationMapFree.put(location.getIddriver(), location);
			}else if(location.getStatus().equals(WebSocketBookingTitle.BUSY.toString())) {
				locationMapBusy.put(location.getIddriver(), location);
			}
		}
	}


	public List<LocationDriver> getByStatus(String status) {
		List<LocationDriver> locaList = new ArrayList<>();
		for (LocationDriver locaDriver : locationMapFree.values()) {
			if (locaDriver.getStatus().equals(status)) {
				locaList.add(locaDriver);
			}
		}
		return locaList;
	}

	public boolean checkAddOrUpdate(int idDriver , String status) {
		if(status.equals(WebSocketBookingTitle.FREE.toString())) {
			return locationMapFree.containsKey(idDriver);
		} else  {
			return locationMapBusy.containsKey(idDriver);
		}
	}
}
