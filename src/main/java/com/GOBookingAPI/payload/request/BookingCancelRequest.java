package com.GOBookingAPI.payload.request;

import com.GOBookingAPI.enums.ReasonType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCancelRequest {
	@NotNull
	private ReasonType reasonType;

	private String content;
}
