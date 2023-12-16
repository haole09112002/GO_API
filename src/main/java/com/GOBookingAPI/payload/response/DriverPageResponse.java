package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.utils.DriverStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverPageResponse {
	private int id;
	private String area;
	private String fullname;
	private String phonenumber;
	private DriverStatus status;
	private boolean insonblock;
}
