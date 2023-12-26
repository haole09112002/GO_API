package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BaseReviewResponse {

	private int fiveStar;
	
	private int fourStar;
	
	private int threeStar;
	
	private int TwoStar;
	
	private int OneStar;
	
	
}
