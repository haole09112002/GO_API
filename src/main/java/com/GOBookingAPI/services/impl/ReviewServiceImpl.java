package com.GOBookingAPI.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Review;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.ReviewRequest;
import com.GOBookingAPI.payload.response.BaseResponse;
import com.GOBookingAPI.repositories.BookingRepository;
import com.GOBookingAPI.repositories.ReviewRepository;
import com.GOBookingAPI.services.IReviewService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReviewServiceImpl implements IReviewService{

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Override
	public BaseResponse<Review> createReview(ReviewRequest reviewRequest) {
		try {
			Booking booking = bookingRepository.findById(reviewRequest.getBookingId())
					.orElseThrow(() -> new NotFoundException("Không tìm thấy Booking"));
			Review review = new Review();
			review.setBooking(booking);
			review.setRating(reviewRequest.getRating());
			review.setContent(reviewRequest.getContent());
			Date currentDate = new Date();
			review.setCreateAt(currentDate);
			reviewRepository.save(review);
			return new BaseResponse<Review>( review, "Success");
		}catch(Exception e) {
			log.info("Error ReviewService");
			return new BaseResponse<Review>( null, e.getMessage());
		}
		
	}

}
