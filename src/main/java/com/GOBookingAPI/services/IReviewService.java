package com.GOBookingAPI.services;

import java.util.Date;

import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.payload.response.ReviewResponse;
import com.GOBookingAPI.payload.response.StatisticsReviewResponse;

public interface IReviewService {
	ReviewResponse createReview(ReviewRequest reviewRequest, String email);

	ReviewResponse getReviewById(int id, String email);
	
}
