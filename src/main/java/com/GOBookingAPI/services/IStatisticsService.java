package com.GOBookingAPI.services;

import java.util.Date;

import com.GOBookingAPI.payload.response.StatisticsResponse;

public interface IStatisticsService {
	StatisticsResponse getStatisticsResponse(Date from , Date to , String unit);
}
