package com.GOBookingAPI.services;


import com.GOBookingAPI.payload.request.CreateConservationRequest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IConservationService {
	BaseResponse<?> createConservation(CreateConservationRequest conservationRequest);
}
