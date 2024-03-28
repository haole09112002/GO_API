package com.GOBookingAPI.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.GOBookingAPI.entities.Driver;
import com.GOBookingAPI.entities.User;
import com.GOBookingAPI.enums.BookingStatus;
import com.GOBookingAPI.enums.RoleEnum;
import com.GOBookingAPI.exceptions.AccessDeniedException;
import com.GOBookingAPI.exceptions.BadRequestException;
import com.GOBookingAPI.payload.response.ReviewResponse;
import com.GOBookingAPI.repositories.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GOBookingAPI.entities.Booking;
import com.GOBookingAPI.entities.Review;
import com.GOBookingAPI.exceptions.NotFoundException;
import com.GOBookingAPI.payload.request.ReviewRequest;
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

    @Autowired
    private DriverRepository driverRepository;

    @Override
    @Transactional
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
        review.setId(booking.getId());
        review.setBooking(booking);
        review.setRating(reviewRequest.getRating());
        review.setContent(reviewRequest.getContent());
        review.setCreateAt(new Date());
        reviewRepository.save(review);

        Driver driver = booking.getDriver();
        driver.updateRating(review.getRating());
        driverRepository.save(driver);

        return new ReviewResponse(review);
    }

    @Override
    public ReviewResponse getReviewById(int id, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Khong tim thay user: " + email));
        Review review = reviewRepository.findById(id).orElseThrow(()-> new NotFoundException("Khong tim thay review: " + id));
        if(user.getFirstRole().getName().equals(RoleEnum.CUSTOMER))
            if(review.getBooking().getCustomer().getId() != user.getId())
                throw new AccessDeniedException("Review khong thuoc ve ban");

        if(user.getFirstRole().getName().equals(RoleEnum.DRIVER))
            if(review.getBooking().getDriver().getId() != user.getId())
                throw new AccessDeniedException("Review khong thuoc ve ban");

        return new ReviewResponse(review);
    }

    
    
}
