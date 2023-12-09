package com.GOBookingAPI.utils;

import com.GOBookingAPI.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDriver {
	private int driverId;
	private String location;
	private VehicleType vehicleType;
//	private String status;
}
