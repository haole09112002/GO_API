package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsResponse {
	private StatisticsPaymentResponse statisticsPaymentResponse;
	
	private StatisticsBookingResponse statisticsBookingResponse;
	
	private StatisticsReviewResponse statisticsReviewResponse;
}
