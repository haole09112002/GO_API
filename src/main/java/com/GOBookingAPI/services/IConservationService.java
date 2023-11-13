package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.CreateConservationRequest;

public interface IConservationService {
	String createConservation(CreateConservationRequest conservationRequest);
}
