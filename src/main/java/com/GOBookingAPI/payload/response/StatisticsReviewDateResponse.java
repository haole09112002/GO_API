package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsReviewDateResponse {
	
	private Object Date;
	
	private int totalReivewDate;
	
	private int fiveStar;
	
	private int fourStar;
	
	private int threeStar;
	
	private int twoStar;
	
	private int oneStar;
	
	private String contentPositive;
	
	private String contentNegative;
}
