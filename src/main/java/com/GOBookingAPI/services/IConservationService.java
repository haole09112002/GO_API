package com.GOBookingAPI.services;


import com.GOBookingAPI.payload.request.CreateConservationRequest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IConservationService {
	BaseResponse<?> createConservation(int id_customer , int id_driver ,int id_booking);
}
