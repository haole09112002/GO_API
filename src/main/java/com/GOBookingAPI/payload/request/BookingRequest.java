package com.GOBookingAPI.payload.request;


import com.GOBookingAPI.enums.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

	@NotBlank(message = "PickUpLocation cannot be blank")
	private String pickUpLocation ;
	
	@NotBlank(message = "DropOffLocation cannot be blank")
	private String dropOffLocation;

	@NotNull(message = "VehicleType cannot be null")
	private VehicleType vehicleType;

	public void setVehicleType(VehicleType vehicleType) {
		if (vehicleType != null && Arrays.asList(VehicleType.values()).contains(vehicleType)) {
			this.vehicleType = vehicleType;
		} else {
			throw new IllegalArgumentException("Invalid VehicleType value");
		}
	}
}
