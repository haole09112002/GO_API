package com.GOBookingAPI.services.impl;

import java.util.Date;
import java.util.Optional;

import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.response.ReviewResponse;
import com.GOBookingAPI.repositories.CustomerRepository;
import com.GOBookingAPI.repositories.UserRepository;
import com.GOBookingAPI.services.ICustomerService;
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
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Khong tim thay user: " + email));
        Booking booking = bookingRepository.findById(reviewRequest.getBookingId()).orElseThrow(() -> new NotFoundException("Khong tim thay booking: " + reviewRequest.getBookingId()));

        if (booking.getCustomer().getId() != user.getId()) {
            throw new AccessDeniedException("You don't have permit to access this resource");
        }

        if (!booking.getStatus().equals(BookingStatus.COMPLETE)) {
            throw new BadRequestException("Booking not yet complete");
        }

        if (booking.getReview() != null) {
            throw new BadRequestException("You already reviewed this booking " + reviewRequest.getBookingId());
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setRating(reviewRequest.getRating());
        review.setContent(reviewRequest.getContent());
        review.setCreateAt(new Date());
        reviewRepository.save(review);
        return new ReviewResponse(review);
    }

}
