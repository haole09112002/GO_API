package com.GOBookingAPI.services;

import com.GOBookingAPI.entities.Review;
import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.payload.response.BaseResponse;

public interface IReviewService {
	BaseResponse<Review> createReview(ReviewRequest review);
}
