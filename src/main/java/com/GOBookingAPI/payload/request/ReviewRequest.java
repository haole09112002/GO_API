package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

	private int bookingId;
	
	private int rating;
	
	private String content;
}
