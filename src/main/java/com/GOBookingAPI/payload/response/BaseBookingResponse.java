package com.GOBookingAPI.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseBookingResponse {

	private List<String> timeStamp;
	private List<Integer> total;
	private List<Integer> success ;
	private List<Integer> cancelled ;
}
