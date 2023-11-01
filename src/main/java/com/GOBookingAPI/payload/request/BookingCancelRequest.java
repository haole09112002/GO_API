package com.GOBookingAPI.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCancelRequest {
	private int bookingId ;
	
	private String reasonType;
	
	private String content;
}
