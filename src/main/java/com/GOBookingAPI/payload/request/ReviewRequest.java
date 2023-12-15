package com.GOBookingAPI.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

	@Min(0)
	protected int bookingId;

	@Min(0)
	@Max(5)
	protected int rating;

	@NotBlank
	protected String content;
}
