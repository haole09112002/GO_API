package com.GOBookingAPI.payload.response;

import java.util.Date;

import com.GOBookingAPI.utils.DriverStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverPageResponse {
	private int id;
	private String email;
	private Date createDate;
	private String activityArea;
	private String fullName;
	private String phoneNumber;
	private DriverStatus status;
	private boolean isNonBlock;
}
