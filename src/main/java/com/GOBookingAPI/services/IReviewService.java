package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Review;
import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IReviewService {
	String createReview(ReviewRequest review);
}
