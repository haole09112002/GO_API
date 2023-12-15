package com.GOBookingAPI.services;

import com.GOBookingAPI.payload.request.ReviewRequest;

public interface IReviewService {
	String createReview(ReviewRequest review);
}
