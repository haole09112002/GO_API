package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Review;
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
	public String createReview(ReviewRequest reviewRequest) {
		try {
			Optional<Booking> bookingOptional = bookingRepository.findById(reviewRequest.getBookingId());
			Booking booking = bookingOptional.get();
			Review review = new Review();
			review.setBooking(booking);
			review.setRating(reviewRequest.getRating());
			review.setContent(reviewRequest.getContent());
			Date currentDate = new Date();
			review.setCreateAt(currentDate);
			reviewRepository.save(review);
			return "Success";
		}catch(Exception e) {
			log.info("Error ReviewService {}" , e.getMessage());
			return "Fail";
		}
		
	}

}
