package com.GOBookingAPI.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingWebSocketResponse {
	private int bookingId;
	
	private int customerId;
}
