package com.GOBookingAPI.payload.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsReviewResponse {
	
	private int total;
	
	private double average;

	BaseReviewResponse details;
}
