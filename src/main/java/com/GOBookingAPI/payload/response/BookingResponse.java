package com.GOBookingAPI.payload.response;

import com.GOBookingAPI.entities.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

	private Booking booking;
}
