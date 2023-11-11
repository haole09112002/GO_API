package com.GOBookingAPI.payload.request;


import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

	@NotBlank(message = "PickUpLocation cannot be blank")
	private String pickUpLocation ;
	
	@NotBlank(message = "DropOffLocation cannot be blank")
	private String dropOffLocation;

	@Min(value = 1, message = "Giá trị phải lớn hơn 0")
	private int vehicleType;
}
