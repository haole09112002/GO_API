package com.GOBookingAPI.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDriver {
	private int iddriver;
	private String location;
	private String status;
}
