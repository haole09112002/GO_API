package com.GOBookingAPI.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import org.springframework.stereotype.Service;
@Service
@Getter
public class ManagerBooking {
	private Map<Integer, Integer> clientMap ;

	public ManagerBooking( ) {
		this.clientMap = new HashMap<Integer, Integer>();
	}

	public void AddData(int driverId , int customerId) {
		clientMap.put(driverId,customerId);
	}

	public void deleteData(int driverId) {
		clientMap.remove(driverId);
	}

	public int CheckBooking(int driverId) {
		if(clientMap.containsKey(driverId)) {
			return clientMap.get(driverId);
		}else {
			return 0 ;
		}
	}

	public int  getCustomerIdByDriverId (int driverId){
		if(this.clientMap.containsKey(driverId)){
			return clientMap.get(driverId);
		}
		return -1;
	}



}
