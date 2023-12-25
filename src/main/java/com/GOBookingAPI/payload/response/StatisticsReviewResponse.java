package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsReviewResponse {
	
	private int totalReview;
	
	private int reviewPositive;
	
	private int reviewNegative;
	
	PagedResponse<StatisticsReviewDateResponse> page;
}
