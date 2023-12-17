package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.payload.response.ReviewResponse;

public interface IReviewService {
	ReviewResponse createReview(ReviewRequest reviewRequest, String email);

	ReviewResponse getReviewById(int id, String email);
}
