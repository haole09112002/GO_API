package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsBookingBaseResponse {

	private int totalBooking;
	
	private int totalBookingSuccess;
	
	private int totalBookingFail;
	
	PagedResponse<StatisticsBookingDateResponse> page;
	
}
