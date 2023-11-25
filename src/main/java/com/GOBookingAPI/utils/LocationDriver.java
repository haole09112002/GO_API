package com.GOBookingAPI.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocationDriver {
	private int iddriver;
	private String location;
	private String status;
}
